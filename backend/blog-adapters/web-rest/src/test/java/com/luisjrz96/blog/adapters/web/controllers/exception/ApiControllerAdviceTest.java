package com.luisjrz96.blog.adapters.web.controllers.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.luisjrz96.blog.adapters.web.dto.ApiErrorResponse;
import com.luisjrz96.blog.application.shared.error.ApplicationException;
import com.luisjrz96.blog.application.shared.error.ApplicationUnauthorizedException;
import com.luisjrz96.blog.application.shared.error.NotFoundException;
import com.luisjrz96.blog.domain.exception.DomainException;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class ApiControllerAdviceTest {

  @Mock private Tracer tracer;
  @Mock private HttpServletRequest request;
  @InjectMocks private ApiControllerAdvice advice;

  @Test
  void handleDomainException_shouldReturn400_withMessagePathAndTraceId() {
    // given
    Span span = spanWithTraceId("abc123");
    when(tracer.currentSpan()).thenReturn(span);
    when(request.getRequestURI()).thenReturn("/api/posts/123");
    DomainException ex = new DomainException("Invalid domain state");

    // when
    ResponseEntity<ApiErrorResponse> response = advice.handleDomainException(ex, request);

    // then
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    ApiErrorResponse body = response.getBody();
    assertNotNull(body);

    assertEquals(400, body.getStatus());
    assertEquals("Bad Request", body.getError());
    assertEquals("Invalid domain state", body.getMessage());
    assertEquals("/api/posts/123", body.getPath());
    assertEquals("abc123", body.getTraceId());
    assertNotNull(body.getTimestamp());
    assertEquals(ZoneOffset.UTC, body.getTimestamp().getOffset());
  }

  @Test
  void handleNotFoundException_shouldReturn404_withMessagePathAndTraceId() {
    Span span = spanWithTraceId("t-404");
    when(tracer.currentSpan()).thenReturn(span);
    when(request.getRequestURI()).thenReturn("/api/posts/123");

    NotFoundException ex = new NotFoundException("Not found");

    ResponseEntity<ApiErrorResponse> response = advice.handleNotFoundException(ex, request);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    ApiErrorResponse body = response.getBody();
    assertNotNull(body);
    assertEquals(404, body.getStatus());

    assertEquals("Not Found", body.getError());
    assertEquals("Not found", body.getMessage());
    assertEquals("/api/posts/123", body.getPath());
    assertEquals("t-404", body.getTraceId());
    assertNotNull(body.getTimestamp());
  }

  @Test
  void handleApplicationException_shouldReturn400() {
    Span span = spanWithTraceId("t-400");
    when(tracer.currentSpan()).thenReturn(span);
    when(request.getRequestURI()).thenReturn("/api/posts/123");

    ApplicationException ex = new ApplicationException("Bad input");

    ResponseEntity<ApiErrorResponse> response = advice.handleApplicationException(ex, request);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    ApiErrorResponse body = response.getBody();
    assertNotNull(body);

    assertEquals(400, body.getStatus());
    assertEquals("Bad Request", body.getError());
    assertEquals("Bad input", body.getMessage());
    assertEquals("/api/posts/123", body.getPath());
    assertEquals("t-400", body.getTraceId());
    assertNotNull(body.getTimestamp());
  }

  @Test
  void handleApplicationUnauthorizedException_shouldReturn401() {
    Span span = spanWithTraceId("t-401");
    when(tracer.currentSpan()).thenReturn(span);
    when(request.getRequestURI()).thenReturn("/api/posts/123");

    ApplicationUnauthorizedException ex = new ApplicationUnauthorizedException("Unauthorized");

    ResponseEntity<ApiErrorResponse> response =
        advice.handleApplicationUnauthorizedException(ex, request);

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    ApiErrorResponse body = response.getBody();
    assertNotNull(body);

    assertEquals(401, body.getStatus());
    assertEquals("Unauthorized", body.getError());
    assertEquals("Unauthorized", body.getMessage());
    assertEquals("/api/posts/123", body.getPath());
    assertEquals("t-401", body.getTraceId());
    assertNotNull(body.getTimestamp());
  }

  @Test
  void handleRuntimeException_shouldReturn500_withPathAndTraceId_andNoMessage() {
    Span span = spanWithTraceId("rt-500");
    when(tracer.currentSpan()).thenReturn(span);
    when(request.getRequestURI()).thenReturn("/api/posts/123");

    RuntimeException ex = new RuntimeException("boom");

    ResponseEntity<ApiErrorResponse> response = advice.handleRuntimeException(ex, request);

    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    ApiErrorResponse body = response.getBody();
    assertNotNull(body);

    assertEquals(500, body.getStatus());
    assertEquals("Internal Server Error", body.getError());
    assertEquals("/api/posts/123", body.getPath());
    assertEquals("rt-500", body.getTraceId());

    assertNotNull(body);
    assertNotNull(body.getTimestamp());
  }

  @Test
  void traceId_shouldBeNA_whenNoCurrentSpan() {
    when(tracer.currentSpan()).thenReturn(null);
    DomainException ex = new DomainException("Invalid");

    ResponseEntity<ApiErrorResponse> response = advice.handleDomainException(ex, request);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("N/A", response.getBody().getTraceId());
  }

  private static Span spanWithTraceId(String traceId) {
    Span span = mock(Span.class);
    TraceContext ctx = mock(TraceContext.class);

    when(span.context()).thenReturn(ctx);
    when(ctx.traceId()).thenReturn(traceId);
    return span;
  }
}
