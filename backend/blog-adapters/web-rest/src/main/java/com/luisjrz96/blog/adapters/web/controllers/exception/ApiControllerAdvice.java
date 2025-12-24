package com.luisjrz96.blog.adapters.web.controllers.exception;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.luisjrz96.blog.adapters.web.dto.ApiErrorResponse;
import com.luisjrz96.blog.application.shared.error.ApplicationException;
import com.luisjrz96.blog.application.shared.error.ApplicationUnauthorizedException;
import com.luisjrz96.blog.application.shared.error.NotFoundException;
import com.luisjrz96.blog.domain.exception.DomainException;

import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@ControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class ApiControllerAdvice {

  private final Tracer tracer;

  // ----------- Handle Domain Exceptions -----------
  @ExceptionHandler(DomainException.class)
  public ResponseEntity<ApiErrorResponse> handleDomainException(
      DomainException ex, HttpServletRequest request) {
    HttpStatus status = HttpStatus.BAD_REQUEST;

    ApiErrorResponse errorResponse = new ApiErrorResponse();
    errorResponse.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
    errorResponse.setStatus(status.value());
    errorResponse.setError(status.getReasonPhrase());
    errorResponse.setMessage(ex.getMessage());
    errorResponse.setPath(request.getRequestURI());
    errorResponse.traceId(traceId());
    return ResponseEntity.status(status).body(errorResponse);
  }

  // ----------- Handle Application Exceptions -----------
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNotFoundException(
      NotFoundException ex, HttpServletRequest request) {
    HttpStatus status = HttpStatus.NOT_FOUND;

    ApiErrorResponse errorResponse = new ApiErrorResponse();
    errorResponse.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
    errorResponse.setStatus(status.value());
    errorResponse.setError(status.getReasonPhrase());
    errorResponse.setMessage(ex.getMessage());
    errorResponse.setPath(request.getRequestURI());
    errorResponse.traceId(traceId());
    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<ApiErrorResponse> handleApplicationException(
      ApplicationException ex, HttpServletRequest request) {
    HttpStatus status = HttpStatus.BAD_REQUEST;

    ApiErrorResponse errorResponse = new ApiErrorResponse();
    errorResponse.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
    errorResponse.setStatus(status.value());
    errorResponse.setError(status.getReasonPhrase());
    errorResponse.setMessage(ex.getMessage());
    errorResponse.setPath(request.getRequestURI());
    errorResponse.traceId(traceId());
    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(ApplicationUnauthorizedException.class)
  public ResponseEntity<ApiErrorResponse> handleApplicationUnauthorizedException(
      ApplicationUnauthorizedException ex, HttpServletRequest request) {
    HttpStatus status = HttpStatus.UNAUTHORIZED;

    ApiErrorResponse errorResponse = new ApiErrorResponse();
    errorResponse.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
    errorResponse.setStatus(status.value());
    errorResponse.setError(status.getReasonPhrase());
    errorResponse.setMessage(ex.getMessage());
    errorResponse.setPath(request.getRequestURI());
    errorResponse.traceId(traceId());
    return ResponseEntity.status(status).body(errorResponse);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiErrorResponse> handleRuntimeException(
      RuntimeException ex, HttpServletRequest request) {
    String traceId = traceId();
    log.error(
        "Unhandled exception [traceId={}]: {} {}",
        traceId,
        request.getMethod(),
        request.getRequestURI(),
        ex);

    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    ApiErrorResponse errorResponse = new ApiErrorResponse();
    errorResponse.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
    errorResponse.setStatus(status.value());
    errorResponse.setError(status.getReasonPhrase());
    errorResponse.setPath(request.getRequestURI());
    errorResponse.traceId(traceId);
    return ResponseEntity.status(status).body(errorResponse);
  }

  private String traceId() {
    return tracer.currentSpan() != null ? tracer.currentSpan().context().traceId() : "N/A";
  }
}
