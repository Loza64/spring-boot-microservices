package com.app.auth_service.controller;

import com.app.auth_service.common.exceptions.*;
import com.app.auth_service.common.exceptions.response.ExceptionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class AdviceController {

  private static final Logger log = LoggerFactory.getLogger(AdviceController.class);

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ExceptionResponse> handleUnauthorized(UnauthorizedException ex) {
    return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
  }

  // ── Autorización ────────────────────────────────────────────────────────

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ExceptionResponse> handleAccessDenied(AccessDeniedException ex) {
    return buildResponse(HttpStatus.FORBIDDEN, "Acceso denegado");
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ExceptionResponse> handleForbidden(ForbiddenException ex) {
    return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
  }

  // ── Recursos / conflictos de negocio ───────────────────────────────────

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ExceptionResponse> handleNotFound(NotFoundException ex) {
    return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(ConfictException.class)
  public ResponseEntity<ExceptionResponse> handleConflict(ConfictException ex) {
    return buildResponse(HttpStatus.CONFLICT, ex.getMessage());
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ExceptionResponse> handleBadRequest(BadRequestException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(ServerException.class)
  public ResponseEntity<ExceptionResponse> handleServerException(ServerException ex) {
    log.error("ServerException: {}", ex.getMessage(), ex);
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
  }

  // ── Validación de entrada ──────────────────────────────────────────────

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionResponse> handleValidation(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult().getFieldErrors()
        .stream()
        .map(err -> err.getField() + ": " + err.getDefaultMessage())
        .collect(Collectors.joining(", "));
    return buildResponse(HttpStatus.BAD_REQUEST, message.isEmpty() ? "Datos inválidos" : message);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ExceptionResponse> handleMissingParams(MissingServletRequestParameterException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, "Falta el parámetro requerido: " + ex.getParameterName());
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ExceptionResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, "Tipo de parámetro inválido: " + ex.getName());
  }

  // ── Fallback ────────────────────────────────────────────────────────────

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ExceptionResponse> handleRuntime(RuntimeException ex) {
    log.warn("RuntimeException sin handler específico: {}", ex.getClass().getSimpleName(), ex);
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionResponse> handleGeneric(Exception ex) {
    log.error("Error no controlado", ex);
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
  }

  private ResponseEntity<ExceptionResponse> buildResponse(HttpStatus status, String message) {
    ExceptionResponse response = new ExceptionResponse(status.value(), message);
    return ResponseEntity.status(status).body(response);
  }
}