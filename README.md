# Documentación — `spring-boot-microservices` en Kubernetes

Namespace: `spring-microservices`

---

## 1. Arquitectura

```
Internet ──> gateway-service (LoadBalancer :80 → :5000)
                 ├──> auth-service (:5001)  ──┐
                 └──> user-service (:5002)  ──┼──> postgres-service (:5432)
                                              │      ├─ auth_db
                                              │      └─ user_db
```

- **gateway-service**: único punto de entrada expuesto (Spring Cloud Gateway). Enruta `/api/auth/**` → auth-service y `/api/users/**` → user-service. No toca la base de datos (bloqueado explícitamente por NetworkPolicy).
- **auth-service**: login/registro, emite y firma JWT. Habla con user-service vía HTTP interno (header `X-Internal-Api-Key`) para consultar/crear usuarios.
- **user-service**: dueño de los datos de usuario, valida JWT, expone endpoints internos protegidos por `X-Internal-Api-Key`, siembra un usuario super-admin al arrancar.
- **postgres**: una sola instancia (`Recreate` + PVC `ReadWriteOnce`), con dos bases: `auth_db` (default) y `user_db` (creada por un init script).

---

## 2. Estructura de archivos

```
.
├── kustomization.yaml
├── .env                          # Secret compartido: app-secrets
├── user-service/
│   └── .env                      # Secret exclusivo de user-service: user-service-env
└── k8s/
    ├── 00-namespace.yaml
    ├── 01-configmap.yaml          # ConfigMap: app-config (no sensible)
    ├── 02-postgres-deployment.yaml
    ├── 03-auth-deployment.yaml
    ├── 04-user-deployment.yaml
    ├── 05-gateway-deployment.yaml
    └── 06-networkpolicy.yaml
```

`auth_service/.env` y `gateway_service/.env` **ya no existen** — se eliminaron junto con sus `secretGenerator`/`secretRef`, porque ninguno de esos dos servicios necesitaba variables propias que no pudieran vivir en `app-config` o `app-secrets`.

---

## 3. Dónde vive cada variable (y por qué)

| Origen | Tipo | Contenido | Quién lo consume |
|---|---|---|---|
| `k8s/01-configmap.yaml` → ConfigMap `app-config` | No sensible | `AUTH_MICROSERVICE`, `USER_MICROSERVICE`, `JWT_ISSUER` | auth-service, user-service, gateway-service |
| `.env` (raíz) → Secret `app-secrets` | Sensible | `DB_USERNAME`, `DB_PASSWORD`, `INTERNAL_API_KEY`, `JWT_SECRET` | postgres, auth-service, user-service, gateway-service |
| `user-service/.env` → Secret `user-service-env` | Sensible, exclusivo | `SUPER_ADMIN_EMAIL`, `SUPER_ADMIN_PASSWORD`, `SUPER_ADMIN_USERNAME` | solo user-service |
| Directo en cada Deployment (`env:`) | Fijo, no secreto | `SERVER_PORT`, `DB_URL` | cada servicio, por separado |

Regla de diseño aplicada: **lo que no es secreto va en ConfigMap; lo que es secreto y compartido va en `app-secrets`; lo que es secreto y exclusivo de un servicio va en su propio Secret.** Nada queda duplicado entre ConfigMap y Secret.

---

## 4. Cómo deben verse los `.env` para que todo funcione

### 4.1 `.env` (raíz del proyecto)

```env
#db auth
DB_PASSWORD=12345678
DB_USERNAME=postgres

#header endpoint config
INTERNAL_API_KEY=S0RGSkZHS0dHSkZER0tETUtEw5FPUFNSR0pFV0dQRUlKRVBJRkpQRklKUzM0OTU4MzQwOTU4QUtTRkpTRExLTkFTTEtGTkpBS0ZKQUzDkVNLU0RGSkFT
JWT_SECRET=R0VSMzQwUlVXUTA4OVJVUU1XRElPUVdKUkpFV09QUklFV0RCR0RCR0hKR0hKR0hOR0hOR0dGR1NHVkRSRVNIRkRLTlZTRE5GRFNLRk5EU0ZERktEU0FGS1NESkZTQURMS0pGU0RLRkRTS0xGSlNETEtGU0pGTEtTREFKRkxL
```

- `DB_USERNAME` / `DB_PASSWORD`: credenciales de Postgres. Los usa el propio Postgres (`POSTGRES_USER`/`POSTGRES_PASSWORD`) y también auth-service/user-service para conectarse (`spring.datasource.username/password`).
- `INTERNAL_API_KEY`: debe ser **exactamente igual** en auth-service y user-service — auth-service la manda en el header `X-Internal-Api-Key` al llamar endpoints internos de user-service, que la compara byte a byte. Como ambos la leen del mismo Secret, siempre coinciden.
- `JWT_SECRET`: debe ser **exactamente igual** en auth-service (firma tokens) y user-service (los valida). Misma razón: al venir del mismo Secret, no hay riesgo de desincronización.

### 4.2 `user-service/.env`

```env
SUPER_ADMIN_EMAIL=robertoloxa88@gmail.com
SUPER_ADMIN_PASSWORD=passW1234-
SUPER_ADMIN_USERNAME=loza.dev
```

Solo lo lee `user-service` (siembra una cuenta admin al arrancar). No tiene sentido exponerlo a auth-service ni gateway-service, por eso vive separado.

### 4.3 Lo que **no** va en ningún `.env`

Ya está resuelto en el propio código YAML, no lo dupliques en un `.env`:

- `JWT_ISSUER`, `AUTH_MICROSERVICE`, `USER_MICROSERVICE` → van en `k8s/01-configmap.yaml`, no en ningún Secret.
- `SERVER_PORT`, `DB_URL` → hardcodeados en cada Deployment (`03-`, `04-`, `05-`).

### 4.4 Checklist antes de desplegar

- [ ] `.env` (raíz) existe, con `DB_USERNAME`, `DB_PASSWORD`, `INTERNAL_API_KEY`, `JWT_SECRET`.
- [ ] `user-service/.env` existe, con los 3 `SUPER_ADMIN_*`.
- [ ] Ninguno de los dos está en git (agrégalos a `.gitignore`).
- [ ] `k8s/01-configmap.yaml` tiene el `JWT_ISSUER` que realmente quieres usar (ej. `auth-token-microservice-issuer`).

---

## 5. CREAR (despliegue inicial) — con Kustomize

```bash
kubectl apply -k .
```

Verificar:

```bash
kubectl get all -n spring-microservices
kubectl get pods -n spring-microservices -w
kubectl get secrets -n spring-microservices
```

Los pods de `auth-service` y `user-service` pueden tardar 15–30s en pasar a `1/1 Running` mientras Spring Boot termina de levantar el contexto y conectarse a Postgres — el `startupProbe` les da hasta ~2.5 minutos de margen antes de reiniciarlos, así que un `0/1 Running` de los primeros segundos es normal, no un error.

Obtener acceso al gateway:

```bash
kubectl get svc gateway-service -n spring-microservices
kubectl port-forward svc/gateway-service -n spring-microservices 8080:80
minikube service gateway-service -n spring-microservices
```

---

## 6. ACTUALIZAR

| Qué cambió | Comando |
|---|---|
| Un YAML (deployment, probes, recursos) | `kubectl apply -k .` |
| El `.env` raíz o `user-service/.env` | `kubectl apply -k .` (Kustomize regenera el Secret con hash nuevo y dispara rollout automático de quien lo use) |
| Versión de una imagen | editar `image:` + `kubectl apply -k .`, o `kubectl set image deployment/auth-service auth-service=loza64/auth-service:1.0.3 -n spring-microservices` |
| Seguir un rollout | `kubectl rollout status deployment/auth-service -n spring-microservices` |
| Revertir un rollout | `kubectl rollout undo deployment/auth-service -n spring-microservices` |
| Forzar reinicio sin cambiar nada | `kubectl rollout restart deployment/auth-service -n spring-microservices` |
| Escalar réplicas (no en Postgres) | `kubectl scale deployment/user-service -n spring-microservices --replicas=3` |

---

## 7. ELIMINAR

```bash
kubectl delete -k .
kubectl delete deployment auth-service -n spring-microservices
kubectl delete namespace spring-microservices
```

⚠️ Borrar el namespace o el PVC implica **perder los datos de Postgres**, salvo que el `PersistentVolume` subyacente tenga `reclaimPolicy: Retain`.

---

## 8. AGREGAR un microservicio nuevo

1. Crea `k8s/07-<nombre>-deployment.yaml` (Deployment + Service), siguiendo el patrón de `03-auth-deployment.yaml`: `envFrom` con `configMapRef: app-config` y `secretRef: app-secrets` como mínimo.
2. Regístralo en `kustomization.yaml`, dentro de `resources`.
3. Si necesita secretos propios (como hicimos con `user-service`): agrega un `<nombre>/.env`, una entrada en `secretGenerator`, y un `secretRef` extra en su Deployment.
4. Si necesita hablar con Postgres, agrégalo al `podSelector` de `postgres-allow-only-services` en `06-networkpolicy.yaml`.
5. `kubectl apply -k .`

Para quitarlo: elimina su entrada de `resources`/`secretGenerator` y borra el recurso aparte con `kubectl delete -f k8s/07-<nombre>-deployment.yaml -n spring-microservices` (Kustomize no lo borra solo).

---

## 9. Todo lo anterior, sin Kustomize (`kubectl apply -f`)

### Crear

```bash
kubectl apply -f k8s/00-namespace.yaml

kubectl create secret generic app-secrets \
  -n spring-microservices --from-env-file=.env

kubectl create secret generic user-service-env \
  -n spring-microservices --from-env-file=user-service/.env

kubectl apply -f k8s/
```

(Los nombres `00-`, `01-`... ya garantizan que se apliquen en ese orden si usas el directorio completo.)

### Actualizar

```bash
kubectl apply -f k8s/03-auth-deployment.yaml

kubectl create secret generic app-secrets \
  -n spring-microservices --from-env-file=.env \
  --dry-run=client -o yaml | kubectl apply -f -
kubectl rollout restart deployment/auth-service -n spring-microservices
kubectl rollout restart deployment/user-service -n spring-microservices
kubectl rollout restart deployment/gateway-service -n spring-microservices
```

### Eliminar

```bash
kubectl delete -f k8s/
kubectl delete secret app-secrets user-service-env -n spring-microservices
```

### Agregar un microservicio nuevo

```bash
kubectl create secret generic <nombre>-env \
  -n spring-microservices --from-env-file=<nombre>/.env
kubectl apply -f k8s/07-<nombre>-deployment.yaml
```

---

## 10. Comandos de verificación / debug útiles

```bash
kubectl exec -it deploy/user-service -n spring-microservices -- printenv | grep DB_URL
kubectl exec -it deploy/user-service -n spring-microservices -- printenv | Select-String DB_URL
kubectl exec -it deploy/user-service -n spring-microservices -- printenv | findstr DB_URL
kubectl logs deploy/auth-service -n spring-microservices
kubectl describe pod -n spring-microservices -l app=auth-service
kubectl get secrets -n spring-microservices
```

---

## 11. Manifiestos finales completos

### `kustomization.yaml`

```yaml
namespace: spring-microservices

resources:
  - k8s/00-namespace.yaml
  - k8s/01-configmap.yaml
  - k8s/02-postgres-deployment.yaml
  - k8s/03-auth-deployment.yaml
  - k8s/04-user-deployment.yaml
  - k8s/05-gateway-deployment.yaml
  - k8s/06-networkpolicy.yaml

secretGenerator:
  - name: app-secrets
    envs:
      - .env
  - name: user-service-env
    envs:
      - user-service/.env
```

### `k8s/01-configmap.yaml`

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
  namespace: spring-microservices
data:
  AUTH_MICROSERVICE: "http://auth-service:5001"
  USER_MICROSERVICE: "http://user-service:5002"
  JWT_ISSUER: "auth-token-microservice-issuer"
```

### `k8s/03-auth-deployment.yaml` (envFrom relevante)

```yaml
          envFrom:
            - configMapRef:
                name: app-config
            - secretRef:
                name: app-secrets
```

### `k8s/04-user-deployment.yaml` (envFrom relevante)

```yaml
          envFrom:
            - configMapRef:
                name: app-config
            - secretRef:
                name: app-secrets
            - secretRef:
                name: user-service-env
```

### `k8s/05-gateway-deployment.yaml` (envFrom relevante)

```yaml
          envFrom:
            - configMapRef:
                name: app-config
            - secretRef:
                name: app-secrets
```
