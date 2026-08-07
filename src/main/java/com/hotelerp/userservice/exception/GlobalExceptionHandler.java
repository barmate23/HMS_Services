package com.hotelerp.userservice.exception;

import com.hotelerp.userservice.common.LogContext;
import com.hotelerp.userservice.common.StandardResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        String logId = LogContext.getLogId();
        log.error("logId: {} - Resource not found: {}", logId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(StandardResponse.error(ex.getMessage(), "RESOURCE_NOT_FOUND",
                        "The requested resource was not found"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        String logId = LogContext.getLogId();
        log.error("logId: {} - Validation error", logId);

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        StandardResponse<Map<String, String>> response = StandardResponse.<Map<String, String>>builder()
                .success(false)
                .message("Validation failed")
                .data(validationErrors)
                .logId(logId)
                .requestId(LogContext.getRequestId())
                .timestamp(LocalDateTime.now())
                .error(StandardResponse.ErrorDetails.builder()
                        .code("VALIDATION_FAILED")
                        .message("Input validation failed")
                        .details("One or more fields have invalid values")
                        .build())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<StandardResponse<Void>> handleCustomException(CustomException ex) {
        String logId = LogContext.getLogId();
        log.error("logId: {} - Custom exception: {}", logId, ex.getMessage());
        return ResponseEntity.badRequest()
                .body(StandardResponse.<Void>builder()
                        .success(false)
                        .message(ex.getMessage())
                        .logId(logId)
                        .requestId(LogContext.getRequestId())
                        .timestamp(LocalDateTime.now())
                        .error(StandardResponse.ErrorDetails.builder()
                                .code(ex.getErrorCode() != null ? ex.getErrorCode() : "CUSTOM_ERROR")
                                .message(ex.getMessage())
                                .details(ex.getDetails())
                                .build())
                        .build());
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<StandardResponse<Void>> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        String logId = LogContext.getLogId();
        log.error("logId: {} - Data integrity violation: {}", logId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(StandardResponse.error(
                        "A recipe or record already exists for this menu item.",
                        "DUPLICATE_ERROR",
                        ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<StandardResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        String logId = LogContext.getLogId();
        log.error("logId: {} - Invalid argument: {}", logId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(StandardResponse.error(ex.getMessage(), "INVALID_INPUT", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardResponse<Void>> handleGenericException(Exception ex) {
        String logId = LogContext.getLogId();
        log.error("logId: {} - Unexpected error: ", logId, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(StandardResponse.error("An unexpected error occurred", "INTERNAL_SERVER_ERROR",
                        "Please contact support. LogID: " + logId));
    }
}
