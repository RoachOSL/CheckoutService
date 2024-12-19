package com.pragmaticcoders.checkout.exceptionhandling;

import com.pragmaticcoders.checkout.exceptionhandling.exceptions.ItemNotFoundException;
import com.pragmaticcoders.checkout.exceptionhandling.exceptions.PromotionNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Set;

@Slf4j
@RestControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    protected final ResponseEntity<ErrorDetails> handleAllException(Exception ex, WebRequest request) {
        log.error("Handling general exception: {}", ex.getMessage(), ex);

        ErrorDetails errorDetails = buildErrorDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected final ResponseEntity<ErrorDetails> handleIllegalArgumentException(IllegalArgumentException ex,
                                                                                WebRequest request) {
        log.error("Illegal argument: {}", ex.getMessage(), ex);

        ErrorDetails errorDetails = buildErrorDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    protected final ResponseEntity<ErrorDetails> handleInventoryNotFoundException(Exception ex, WebRequest request) {
        log.error("Item not found: {}", ex.getMessage(), ex);

        ErrorDetails errorDetails = buildErrorDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PromotionNotFoundException.class)
    protected final ResponseEntity<ErrorDetails> handleProductNotFoundException(Exception ex, WebRequest request) {
        log.error("Promotion not found: {}", ex.getMessage(), ex);

        ErrorDetails errorDetails = buildErrorDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("Method argument not valid: {}", ex.getMessage(), ex);

        List<String> errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField().toUpperCase() + ": " + fieldError.getDefaultMessage())
                .toList();

        String combinedErrorMessage = String.join(".; ", errorMessages);

        ErrorDetails errorDetails = buildErrorDetail(
                HttpStatus.BAD_REQUEST,
                combinedErrorMessage,
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("Missing required parameter: {}", ex.getParameterName(), ex);

        String errorMessage = "Missing required parameter: " + ex.getParameterName();

        ErrorDetails errorDetails = buildErrorDetail(
                HttpStatus.BAD_REQUEST,
                errorMessage,
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String unsupportedMethod = ex.getMethod();
        Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();

        log.error("HTTP method not supported: {}. Supported methods are: {}", unsupportedMethod, supportedMethods, ex);

        String errorMessage = "Method '" + unsupportedMethod + "' is not supported. Supported methods are: "
                + supportedMethods;

        ErrorDetails errorDetails = buildErrorDetail(
                HttpStatus.METHOD_NOT_ALLOWED,
                errorMessage,
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected final ResponseEntity<ErrorDetails> handleConstraintViolationException(ConstraintViolationException ex,
                                                                                    WebRequest request) {
        log.error("Constraint violation: {}", ex.getMessage(), ex);

        List<String> errorMessages = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        String combinedErrorMessage = String.join(".; ", errorMessages);

        ErrorDetails errorDetails = buildErrorDetail(
                HttpStatus.BAD_REQUEST,
                combinedErrorMessage,
                request.getDescription(false)
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    private ErrorDetails buildErrorDetail(HttpStatus httpStatus, String message, String description) {
        return ErrorDetails.builder()
                .status(httpStatus.value())
                .message(message)
                .description(description)
                .build();
    }
}
