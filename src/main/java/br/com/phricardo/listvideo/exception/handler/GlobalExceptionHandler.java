package br.com.phricardo.listvideo.exception.handler;

import static org.springframework.http.HttpStatus.*;

import br.com.phricardo.listvideo.exception.EmailNotVerifiedException;
import br.com.phricardo.listvideo.exception.LoginException;
import br.com.phricardo.listvideo.exception.RegistrationException;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.IllegalFormatConversionException;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<?> handleNotFound(EntityNotFoundException ex) {
    return ResponseEntity.status(NOT_FOUND)
        .body(new ErrorResponse(NOT_FOUND.value(), ex.getLocalizedMessage(), "ENTITY_NOT_FOUND"));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleBadRequest(MethodArgumentNotValidException ex) {
    final var errors = ex.getFieldErrors().stream().map(ValidatedErrors::new).toList();
    return ResponseEntity.status(BAD_REQUEST)
        .body(new ErrorResponse(BAD_REQUEST.value(), errors, "METHOD_ARGUMENT_NOT_VALID"));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<?> handleBadRequest(HttpMessageNotReadableException ex) {
    return ResponseEntity.status(BAD_REQUEST)
        .body(
            new ErrorResponse(
                BAD_REQUEST.value(),
                "Invalid request payload: please check that your request payload is a valid JSON object and that all required fields are present.",
                "INVALID_REQUEST_PAYLOAD"));
  }

  @ExceptionHandler({BadCredentialsException.class, LoginException.class})
  public ResponseEntity<?> handleBadCredentials() {
    return ResponseEntity.status(UNAUTHORIZED)
        .body(
            new ErrorResponse(
                UNAUTHORIZED.value(),
                "Login does not exist or password is invalid",
                "INVALID_LOGIN_CREDENTIALS"));
  }

  @ExceptionHandler(EmailNotVerifiedException.class)
  public ResponseEntity<?> handleEmailNotVerified(EmailNotVerifiedException ex) {
    return ResponseEntity.status(UNAUTHORIZED)
        .body(
            new ErrorResponse(
                UNAUTHORIZED.value(), ex.getLocalizedMessage(), "USER_EMAIL_NOT_VERIFIED"));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<?> handleAuthenticationFailure() {
    return ResponseEntity.status(UNAUTHORIZED)
        .body(
            new ErrorResponse(
                UNAUTHORIZED.value(), "Authentication failed", "AUTHENTICATION_FAILED"));
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<?> handleAccessDenied() {
    return ResponseEntity.status(FORBIDDEN)
        .body(new ErrorResponse(FORBIDDEN.value(), "Access denied", "ACCESS_DENIED"));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
    var message = ex.getLocalizedMessage();
    final var constraintName = ex.getMostSpecificCause().getMessage();
    if (constraintName.startsWith("Duplicate entry")) {
      String[] parts = constraintName.split("'");
      final var value = parts[1];
      final var column = parts[3].substring(parts[3].lastIndexOf(".") + 1);
      message = String.format("The %s '%s' is already registered", column, value);
    }
    return ResponseEntity.status(CONFLICT)
        .body(new ErrorResponse(CONFLICT.value(), message, "DATA_INTEGRITY_VIOLATION"));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<?> handleNoSuchElement(NoSuchElementException ex) {
    return ResponseEntity.status(NOT_FOUND)
        .body(new ErrorResponse(NOT_FOUND.value(), ex.getLocalizedMessage(), "NO_SUCH_ELEMENT"));
  }

  @ExceptionHandler(RegistrationException.class)
  public ResponseEntity<?> handleRegistration(RegistrationException ex) {
    return ResponseEntity.status(BAD_REQUEST)
        .body(
            new ErrorResponse(BAD_REQUEST.value(), ex.getLocalizedMessage(), "REGISTRATION_ERROR"));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
    return ResponseEntity.status(UNAUTHORIZED)
        .body(
            new ErrorResponse(UNAUTHORIZED.value(), ex.getLocalizedMessage(), "ILLEGAL_ARGUMENT"));
  }

  @ExceptionHandler(IllegalFormatConversionException.class)
  public ResponseEntity<?> handleIllegalFormatConversion(IllegalFormatConversionException ex) {
    return ResponseEntity.status(BAD_REQUEST)
        .body(
            new ErrorResponse(
                BAD_REQUEST.value(),
                "Invalid input format: the provided value cannot be converted to the expected data type.",
                "ILLEGAL_FORMAT_CONVERSION"));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleInternalServerError(Exception ex) {
    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
        .body(
            new ErrorResponse(
                INTERNAL_SERVER_ERROR.value(), ex.getLocalizedMessage(), "INTERNAL_SERVER_ERROR"));
  }

  private record ValidatedErrors(String field, String message) {
    public ValidatedErrors(FieldError error) {
      this(error.getField(), error.getDefaultMessage());
    }
  }

  public record ErrorResponse(
      LocalDateTime date, int httpStatusCode, Object message, String errorCode) {
    public ErrorResponse(int statusCode, String message, String errorCode) {
      this(LocalDateTime.now(), statusCode, message, errorCode);
    }

    public ErrorResponse(int statusCode, List<?> messageList, String errorCode) {
      this(LocalDateTime.now(), statusCode, messageList, errorCode);
    }
  }
}
