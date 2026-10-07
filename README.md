# Spring Boot Microservices

Repositorio de una aplicación basada en tres servicios:

| Servicio | Responsabilidad | Puerto local |
|---|---|---:|
| `gateway_service` | Punto de entrada HTTP y enrutamiento | 5000 |
| `auth_service` | Login, registro, JWT y refresh tokens | 5001 |
| `user-service` | Usuarios, perfil, roles y permisos | 5002 |

`user-service` es propietario de los datos de usuarios. `auth_service` obtiene los datos necesarios para autenticar a una persona mediante una API interna protegida con `X-Internal-Api-Key`. Cada servicio usa su propia base de datos PostgreSQL: `auth_db` y `user_db`.

Esta guía cubre el inicio desde cero en Windows/PowerShell, tanto localmente como en Minikube. No ejecuta cambios en un clúster ni elimina datos.

## Requisitos

- Java 21.
- Docker Desktop con Docker Engine activo.
- Git.
- Para Kubernetes: Minikube y `kubectl`.
- Recursos recomendados para Minikube: 4 CPU y 6 GB de memoria.

Los microservicios incluyen Maven Wrapper (`mvnw.cmd`); no es necesario instalar Maven por separado.

## 1. Configurar variables locales

Los archivos `.env` no se versionan. Crea o completa los siguientes archivos localmente; reemplaza todos los valores de ejemplo y no compartas ni publiques los secretos.

### `.env` en la raíz

Este archivo proporciona los valores comunes que Kustomize carga como `app-secrets`. No es necesario incluir `DB_URL`: en Kubernetes cada Deployment configura la URL de su propia base (`auth_db` o `user_db`).

```dotenv
DB_USERNAME=postgres
DB_PASSWORD=CAMBIAR_POR_LA_CLAVE_REAL_DE_POSTGRES
JWT_SECRET=CAMBIAR_POR_UN_SECRETO_ALEATORIO_DE_AL_MENOS_32_BYTES
JWT_ISSUER=auth-token-microservice-issuer
INTERNAL_API_KEY=CAMBIAR_POR_UN_SECRETO_ALEATORIO_COMPARTIDO
```

Genera secretos únicos y aleatorios para `JWT_SECRET` e `INTERNAL_API_KEY`. Usa el mismo `JWT_SECRET`, `JWT_ISSUER` e `INTERNAL_API_KEY` en ambos microservicios.

### `auth_service/.env`

```dotenv
SERVER_PORT=5001
DB_URL=jdbc:postgresql://localhost:5432/auth_db
DB_USERNAME=postgres
DB_PASSWORD=CAMBIAR_POR_LA_CLAVE_REAL_DE_POSTGRES
JWT_SECRET=CAMBIAR_POR_EL_MISMO_SECRETO_COMPARTIDO
JWT_ISSUER=auth-token-microservice-issuer
INTERNAL_API_KEY=CAMBIAR_POR_LA_MISMA_CLAVE_INTERNA_COMPARTIDA
USER_MICROSERVICE=http://localhost:5002
```

### `user-service/.env`

Conserva cualquier configuración que ya tengas y verifica que el archivo contenga todas estas propiedades. Los valores `SUPER_ADMIN_*` se usan para crear el usuario inicial cuando todavía no existe un usuario con rol `SUPER_ADMIN`.

```dotenv
SERVER_PORT=5002
DB_URL=jdbc:postgresql://localhost:5432/user_db
DB_USERNAME=postgres
DB_PASSWORD=CAMBIAR_POR_LA_CLAVE_REAL_DE_POSTGRES
JWT_SECRET=CAMBIAR_POR_EL_MISMO_SECRETO_COMPARTIDO
JWT_ISSUER=auth-token-microservice-issuer
INTERNAL_API_KEY=CAMBIAR_POR_LA_MISMA_CLAVE_INTERNA_COMPARTIDA
SUPER_ADMIN_USERNAME=CAMBIAR_USUARIO_ADMIN
SUPER_ADMIN_EMAIL=admin@example.local
SUPER_ADMIN_PASSWORD=CAMBIAR_POR_UNA_CLAVE_SEGURA
```

`JWT_SECRET` debe tener al menos 32 bytes para el algoritmo de firma configurado. En despliegues reales, usa un gestor de secretos; no guardes credenciales reales en Git ni en ConfigMaps.

## 2. Iniciar PostgreSQL con Docker

La configuración de PostgreSQL está en [`docker-compose.yaml`](https://github.com/Loza64/docker-containers/blob/main/database/postgres/docker-compose.yaml), en el repositorio [docker-containers](https://github.com/Loza64/docker-containers). Publica el puerto `5432`, crea el contenedor `some-postgres` y mantiene los datos en un volumen Docker. Las aplicaciones esperan las bases `auth_db` y `user_db`, y que PostgreSQL sea accesible desde el host en `localhost:5432`.

Si ya tienes `some-postgres` con ambas bases, no lo recrees; verifica el estado y confirma que las credenciales coincidan con los `.env` de este proyecto:

```powershell
docker ps --filter "name=some-postgres"
docker exec some-postgres psql -U postgres -l
```

Para una instalación nueva, clona el repositorio de Docker (si aún no lo tienes), configura las variables que consume Compose y levanta su definición existente. Usa la misma contraseña en los `.env` de los microservicios:

```powershell
git clone https://github.com/Loza64/docker-containers.git
Push-Location .\docker-containers\database\postgres
$env:POSTGRES_USER = "postgres"
$env:POSTGRES_PASSWORD = "CAMBIAR_POR_UNA_CLAVE_SEGURA"
$env:POSTGRES_DB = "auth_db"
docker compose up -d
Pop-Location
```

Compose crea `auth_db` como base inicial, usando `POSTGRES_DB`; el `app_db` del compose se usa solo cuando no se configura esa variable. Crea `user_db` una sola vez:

```powershell
docker exec some-postgres psql -U $env:POSTGRES_USER -d postgres -c "CREATE DATABASE user_db;"
```

Si `some-postgres` ya existe pero está detenido, puedes iniciarlo con `docker start some-postgres`. Compose crea su red bridge y el volumen `data` declarados en ese repositorio. El volumen conserva los datos aunque se detenga o elimine el contenedor; no lo borres si quieres conservar las bases. Minikube no comparte esa red: Kubernetes llega a PostgreSQL a través del puerto publicado y `host.minikube.internal`.

## 3. Iniciar los servicios localmente

Abre una terminal PowerShell por servicio. Inicia primero `user-service`, ya que `auth_service` lo consulta para autenticar usuarios.

**Terminal 1 — user-service**

```powershell
Set-Location .\user-service
.\mvnw.cmd spring-boot:run
```

**Terminal 2 — auth_service**

```powershell
Set-Location .\auth_service
.\mvnw.cmd spring-boot:run
```

**Terminal 3 — gateway_service**

El gateway no carga `.env`; configura sus tres variables antes de ejecutarlo:

```powershell
Set-Location .\gateway_service
$env:SERVER_PORT = "5000"
$env:AUTH_MICROSERVICE = "http://localhost:5001"
$env:USER_MICROSERVICE = "http://localhost:5002"
.\mvnw.cmd spring-boot:run
```

El punto de entrada local es `http://localhost:5000`. También puedes acceder directamente a cada servicio durante el desarrollo en sus puertos indicados arriba.

## 4. Desplegar en Minikube

### 4.1 Preparar las imágenes

Los manifiestos esperan las imágenes `loza64/auth-service:1.0.5`, `loza64/user-service:1.0.5` y `loza64/gateway-service:1.0.5`. Construye las imágenes desde la raíz del repositorio para que Minikube use el código local. Los Dockerfiles y Deployments fijan el usuario no root con UID `10001`; reconstruye las tres imágenes para que ese UID también exista dentro de ellas:

```powershell
minikube image build -t loza64/auth-service:1.0.5 -f auth_service/Dockerfile auth_service
minikube image build -t loza64/user-service:1.0.5 -f user-service/Dockerfile user-service
minikube image build -t loza64/gateway-service:1.0.5 -f gateway_service/Dockerfile gateway_service
```

### 4.2 Actualizar Kubernetes después de cambiar código

Cada vez que cambies el código de un servicio, reconstruye su imagen dentro de Minikube usando el mismo tag declarado en el Deployment. Por ejemplo, si modificaste `auth_service`:

```powershell
minikube image build -t loza64/auth-service:1.0.5 -f auth_service/Dockerfile auth_service
kubectl apply -k .
kubectl rollout restart deployment/auth-service -n spring-microservices
kubectl rollout status deployment/auth-service -n spring-microservices
```

Para cambios en `user-service` o `gateway_service`, usa respectivamente estos comandos de build y reinicio:

```powershell
minikube image build -t loza64/user-service:1.0.5 -f user-service/Dockerfile user-service
kubectl rollout restart deployment/user-service -n spring-microservices
```

```powershell
minikube image build -t loza64/gateway-service:1.0.5 -f gateway_service/Dockerfile gateway_service
kubectl rollout restart deployment/gateway-service -n spring-microservices
```

Después de cada reinicio, verifica el rollout:

```powershell
kubectl rollout status deployment/user-service -n spring-microservices
kubectl rollout status deployment/gateway-service -n spring-microservices
```

Si modificaste manifiestos, `.env` o ConfigMaps/Secrets, aplica los cambios con `kubectl apply -k .`. Para que Kubernetes descargue inequívocamente una nueva imagen, usa un tag nuevo y actualiza el campo `image` en el Deployment correspondiente; `imagePullPolicy: IfNotPresent` puede reutilizar una imagen local que ya tenga el mismo tag. Al cambiar el tag del manifiesto, `kubectl apply -k .` inicia el rollout y no hace falta ejecutar `rollout restart` manualmente.

### 4.3 Comprobar PostgreSQL desde Minikube

Los manifiestos apuntan a `host.minikube.internal:5432`. Asegúrate de que PostgreSQL siga activo en el host, que publique el puerto 5432 y que las dos bases y credenciales estén listas antes de desplegar.

La red bridge creada por Compose permite la comunicación entre contenedores Docker, pero no conecta directamente los Pods de Minikube a PostgreSQL. El acceso de Kubernetes depende del puerto publicado en el host y de `host.minikube.internal`.

### 4.4 Aplicar los manifiestos por primera vez

Desde la raíz, Kustomize carga el namespace, ConfigMap, Deployments, Services y NetworkPolicies. También genera Secrets a partir de `.env` y `user-service/.env`; ambos archivos deben existir y contener los valores requeridos.

```powershell
kubectl apply -k .
kubectl rollout status deployment/auth-service -n spring-microservices
kubectl rollout status deployment/user-service -n spring-microservices
kubectl rollout status deployment/gateway-service -n spring-microservices
kubectl get pods,services,networkpolicies -n spring-microservices
```

Los manifiestos no instalan ni crean PostgreSQL: usan la instancia Docker existente. Kustomize puede generar Secrets con nuevos sufijos cuando cambia su contenido y actualizar los Deployments para usarlos.

### 4.5 Acceder al gateway y verificar salud

En una terminal separada, mantén abierto el port-forward al gateway:

```powershell
kubectl port-forward -n spring-microservices service/gateway-service 8080:80
```

En otra terminal, comprueba la salud del gateway:

```powershell
curl.exe http://localhost:8080/actuator/health
```

Para inspeccionar Actuator en cada microservicio, ejecuta cada port-forward en su propia terminal y consulta la URL indicada:

```powershell
kubectl port-forward -n spring-microservices service/auth-service 15001:5001
# En otra terminal: curl.exe http://localhost:15001/actuator/health/readiness
```

```powershell
kubectl port-forward -n spring-microservices service/user-service 15002:5002
# En otra terminal: curl.exe http://localhost:15002/actuator/health/readiness
```

Para inspeccionar el estado y los logs:

```powershell
kubectl get pods -n spring-microservices
kubectl describe pods -n spring-microservices
kubectl logs deployment/auth-service -n spring-microservices
kubectl logs deployment/user-service -n spring-microservices
kubectl logs deployment/gateway-service -n spring-microservices
```

### NetworkPolicies

`k8s/06-networkpolicy.yaml` aplica aislamiento por defecto y declara los flujos necesarios entre gateway, auth, user, DNS y PostgreSQL. La aplicación efectiva de las políticas depende de que el CNI del clúster las soporte. Algunos clústeres Minikube creados con la configuración por defecto no aplican NetworkPolicies; para probarlas, usa un clúster con un CNI compatible, por ejemplo Calico. No intentes cambiar el CNI de un clúster existente sin seguir el procedimiento específico y evaluar su impacto.

## Arquitectura

Los dos microservicios de negocio siguen una estructura hexagonal (ports and adapters), organizada alrededor de:

- **API:** controladores HTTP y DTOs de entrada/salida.
- **Aplicación:** casos de uso, servicios y puertos.
- **Dominio:** entidades, reglas y excepciones del negocio.
- **Infraestructura:** persistencia JPA, clientes HTTP, configuración y adaptadores de seguridad.

Las dependencias de aplicación apuntan hacia el dominio. La infraestructura implementa los puertos definidos por las capas internas. El gateway es un servicio Spring Cloud Gateway que enruta las peticiones a los servicios internos; no accede directamente a las bases de datos.

Flujo de autenticación:

1. `auth_service` recibe login o signup.
2. Consulta o registra al usuario en `user-service` mediante la API interna protegida por `X-Internal-Api-Key`.
3. `user-service` valida/guarda la información del usuario y almacena contraseñas con BCrypt.
4. `auth_service` firma el access token JWT y gestiona refresh tokens en `auth_db`.
5. Los servicios validan JWT stateless; el token incluye rol y permisos para las comprobaciones de autorización.

Cada servicio es dueño de su base de datos. No se debe compartir el esquema ni escribir directamente en la base del otro servicio.

## Rutas HTTP

Las rutas públicas de negocio se consumen preferentemente a través del gateway.

| Método | Ruta | Comportamiento |
|---|---|---|
| `POST` | `/api/auth/login` | Autentica y devuelve access token, refresh token y perfil. |
| `POST` | `/api/auth/signup` | Registra un usuario y crea la sesión. |
| `POST` | `/api/auth/refresh` | Rota el refresh token y entrega nuevos tokens. |
| `POST` | `/api/auth/logout` | Revoca la familia del refresh token. |
| `GET` | `/api/auth/profile` | Obtiene el perfil autenticado. |
| `PUT` | `/api/auth/profile` | Actualiza el perfil autenticado. |
| `PUT` | `/api/auth/profile/password` | Cambia la contraseña del perfil autenticado. |
| `GET`, `POST`, `PUT`, `DELETE`, `PATCH` | `/api/users/**` | CRUD de usuarios; `PATCH /{id}/restore` restaura un usuario eliminado. Las operaciones requieren permisos. |
| `GET`, `POST`, `PUT`, `DELETE`, `PATCH` | `/api/roles/**` | CRUD de roles; `PATCH /{id}/restore` restaura un rol eliminado. Las operaciones requieren permisos. |
| `GET`, `PUT` | `/api/permissions/**` | Consulta permisos y permite editar sus títulos. |

Los endpoints `/api/internal/auth/**` pertenecen a `user-service`: son para comunicación servicio-a-servicio, exigen `X-Internal-Api-Key` y no se exponen como rutas del gateway.

## Reglas de negocio implementadas

### Usuarios y registro

- El username y el email deben ser únicos; los duplicados impiden el alta.
- Signup valida los datos y requiere una contraseña de al menos 8 caracteres. La contraseña se guarda con BCrypt, nunca en texto plano.
- Signup asigna el rol `CLIENT` por su ID `3`. Esta regla depende de que los seeds mantengan ese ID; al cambiar la inicialización de roles, hay que cambiarla para resolver el rol por nombre y no por ID.
- El superadministrador inicial se crea desde `SUPER_ADMIN_USERNAME`, `SUPER_ADMIN_EMAIL` y `SUPER_ADMIN_PASSWORD` solo si todavía no existe un usuario con rol `SUPER_ADMIN`. Si el username o email ya está ocupado, se omite la creación y se registra un warning.
- `SUPER_ADMIN` no puede modificarse, eliminarse ni restaurarse mediante las operaciones CRUD de usuario.
- El borrado y la restauración de usuarios son lógicos mediante `deletedAt`; no eliminan físicamente el registro.
- Las búsquedas de usuarios admiten paginación y filtros de búsqueda, rol y estado eliminado.

### Perfil

- Las operaciones de perfil usan el ID del usuario autenticado en el JWT; el cliente no elige el ID del perfil.
- Se pueden actualizar nombre, apellido y email, siempre que el email siga siendo único.
- Para cambiar la contraseña se requiere la contraseña actual; la nueva debe tener al menos 8 caracteres.

### Roles

- El nombre del rol debe ser único.
- Los roles admiten borrado lógico y restauración.
- El rol `SUPER_ADMIN` está protegido frente a cambios y borrado.
- Al crear o actualizar un rol se pueden asociar permisos.
- El seed crea `SUPER_ADMIN`, `ADMIN` y `CLIENT`. Actualmente solo `SUPER_ADMIN` recibe todos los permisos automáticamente; `ADMIN` y `CLIENT` se crean sin permisos predeterminados.

### Permisos

- El seed crea permisos con nombres fijos: `USER_CREATE`, `USER_READ`, `USER_UPDATE`, `USER_DELETE`, `ROLE_CREATE`, `ROLE_READ`, `ROLE_UPDATE`, `ROLE_DELETE`, `PERMISSION_READ` y `PERMISSION_UPDATE`.
- `name` es la clave estable del permiso y no se puede actualizar mediante la operación de edición.
- Solo `title` es editable; admite entre 3 y 150 caracteres.
- El seed inicial puede crear `title` vacío/nulo. Si la interfaz necesita mostrar títulos, un usuario autorizado debe establecerlos con la operación de actualización.
- La lectura y actualización de permisos requieren respectivamente `PERMISSION_READ` y `PERMISSION_UPDATE`. El endpoint de seed requiere rol `SUPER_ADMIN`.

### Autenticación y tokens

- Login rechaza credenciales incorrectas y cuentas bloqueadas o eliminadas.
- Los access tokens JWT duran 30 minutos.
- Los refresh tokens duran 7 días, se almacenan hasheados y se rotan al refrescar.
- Reutilizar un token revocado/usado provoca la revocación de su familia; logout también revoca la familia.
- Refresh vuelve a comprobar que el usuario no esté bloqueado ni eliminado.
- La API interna entre auth y user valida la clave `X-Internal-Api-Key`; el secreto debe coincidir en ambos servicios.

## Autorización inicial

`user-service` aplica autorización mediante authorities de permisos y roles incluidos en el JWT. Tras iniciar el sistema, autentícate con el superadministrador configurado. `ADMIN` y `CLIENT` no reciben permisos por defecto en el código actual; así que deben asignarse de forma explícita antes de que puedan operar en endpoints protegidos.

## Diagnóstico y resolución de problemas

- **Pods en `CreateContainerConfigError` con `runAsNonRoot` o usuario no numérico:** reconstruye las imágenes con los Dockerfiles del repositorio (que crean `springboot` con UID `10001`) y vuelve a aplicar/reiniciar los Deployments.
- **Pods en `CrashLoopBackOff`:** consulta `kubectl logs` y confirma que `.env` contiene todas las claves obligatorias.
- **Error de conexión a PostgreSQL:** verifica que el contenedor está activo, que escucha en `5432`, que existen `auth_db` y `user_db`, que el password coincide y que el host Minikube resuelve `host.minikube.internal`. No borres la base para corregirlo.
- **El gateway no conecta a auth/user:** comprueba `app-config`, los Services internos y `kubectl get endpoints -n spring-microservices`.
- **`ImagePullBackOff` o imagen antigua:** construye la imagen en Minikube con el tag exacto declarado en el Deployment y reinicia el Deployment correspondiente.
- **401/403 en user o roles:** 401 indica falta/validez de autenticación; 403 indica que el JWT no tiene el rol o permiso requerido.
- **Secrets desactualizados:** vuelve a aplicar Kustomize después de editar los `.env`: `kubectl apply -k .`; luego observa el rollout de los Deployments.
- **`title` de permisos nulo:** el seed del servicio user completa con el nombre del permiso solo los títulos vacíos; conserva los títulos que ya se personalizaron. Reconstruye `user-service:1.0.5` con el código actualizado y reinicia el Deployment; no es necesario borrar la base ni los Pods manualmente.
- **NetworkPolicies sin efecto:** verifica que el CNI del clúster soporte NetworkPolicy.

## Detener servicios

- Local: detén cada proceso con `Ctrl+C`.
- Kubernetes: elimina los recursos de la aplicación sin tocar PostgreSQL:

  ```powershell
  kubectl delete -k .
  ```

- PostgreSQL: `docker stop some-postgres`. Para conservar los datos, no borres `postgres_data` ni ejecutes `docker volume rm`.
