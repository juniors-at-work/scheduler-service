package com.example.scheduler.domain.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final Clock clock;
    private final String UNEXPECTED_ERROR_MESSAGE = "Неизвестная ошибка";

    public GlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler({
            EntityNotFoundException.class,
            NotFoundException.class
    })
    public ResponseEntity<ApiError> handleEntityNotFound(
            RuntimeException exception,
            ServletWebRequest request
    ) {
        logger.warn("Entity not found: {}", exception.getMessage());
        return toResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequest().getRequestURI()
        );
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingRequestHeaderException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiError> handleBadRequestExceptions(
            Exception exception,
            ServletWebRequest request
    ) {
        logger.warn("Message is not readable: {}", exception.getMessage());
        return toResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage(),
                request.getRequest().getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            ServletWebRequest request
    ) {
        logger.warn("Validation failed: {}", exception.getMessage());
        List<FieldError> errors = exception.getFieldErrors();
        // Fail fast if we cannot present a meaningful message for every error to user
        if (errors.stream().anyMatch(error -> error.getDefaultMessage() == null)) {
            return handleUnexpectedException(exception, request);
        }
        String message = errors.stream()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .sorted()
                .collect(Collectors.joining(", "));
        return toResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request.getRequest().getRequestURI()
        );
    }

    @ExceptionHandler({DataConflictException.class})
    public ResponseEntity<ApiError> handleDataConflictExceptions(
            Exception exception,
            ServletWebRequest request
    ) {
        logger.warn("Data conflict: {}", exception.getMessage());
        return toResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequest().getRequestURI()
        );
    }

    @ExceptionHandler({
            BadCredentialsException.class,
            UserNotAuthorizedException.class,
            InvalidTokenException.class,
            TokenRefreshException.class
    })
    public ResponseEntity<ApiError> handleUnauthorizedExceptions(
            RuntimeException exception,
            ServletWebRequest request
    ) {
        logger.warn("Authorization failed: {}", exception.getMessage());
        return toResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                request.getRequest().getRequestURI()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(
            AccessDeniedException exception,
            ServletWebRequest request
    ) {
        logger.warn(exception.getMessage());

        return toResponse(
                HttpStatus.FORBIDDEN,
                exception.getMessage(),
                request.getRequest().getRequestURI()
        );
    }

    @ExceptionHandler(NotEnoughAuthorityException.class)
    public ResponseEntity<ApiError> handleNotEnoughAuthorityException(
            NotEnoughAuthorityException exception,
            ServletWebRequest request
    ) {
        logger.warn("Operation blocked: {}", exception.getMessage());
        return toResponse(
                HttpStatus.FORBIDDEN,
                exception.getMessage(),
                request.getRequest().getRequestURI()
        );
    }

    @ExceptionHandler({
            UsernameAlreadyExistException.class,
            EmailAlreadyExistException.class
    })
    public ResponseEntity<ApiError> handleConflictExceptions(
            RuntimeException exception,
            ServletWebRequest request
    ) {
        logger.warn(exception.getMessage());
        return toResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequest().getRequestURI()
        );
    }

    @ExceptionHandler(SlotGenerationException.class)
    public ResponseEntity<ApiError> slotGenerationException(
            Exception exception,
            ServletWebRequest request
    ) {
        logger.warn("Failed to generate slots: unexpected exception occurred:>> {}", exception.getMessage(), exception);
        return toResponse(
                HttpStatus.UNPROCESSABLE_ENTITY,
                exception.getMessage(),
                request.getRequest().getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(
            Exception exception,
            ServletWebRequest request
    ) {
        logger.error("Unexpected exception occurred: {}", exception.getMessage(), exception);
        return toResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                UNEXPECTED_ERROR_MESSAGE,
                request.getRequest().getRequestURI()
        );
    }

    @ExceptionHandler(ProfileAlreadyExistException.class)
    public ResponseEntity<ApiError> handleProfileAlreadyExistException(
            ProfileAlreadyExistException exception,
            ServletWebRequest request
    ) {
        logger.warn("Profile already exists: {}", exception.getMessage());
        return toResponse(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequest().getRequestURI()
        );
    }

    @ExceptionHandler({ProfileNotFoundException.class, EventNotFoundException.class})
    public ResponseEntity<ApiError> handleProfileNotFoundException(
            RuntimeException exception,
            ServletWebRequest request
    ) {
        logger.warn(exception.getMessage());
        return toResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequest().getRequestURI()
        );
    }

    private ResponseEntity<ApiError> toResponse(HttpStatus status, String message, String path) {
        ApiError error = new ApiError(
                Instant.now(clock).truncatedTo(ChronoUnit.SECONDS),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(error);
    }
}
