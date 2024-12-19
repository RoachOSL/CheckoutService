package com.pragmaticcoders.checkout.exceptionhandling;

import com.pragmaticcoders.checkout.exceptionhandling.exceptions.ItemNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CustomizedResponseEntityExceptionHandlerTest {

    private static final int INTERNAL_SERVER_ERROR_STATUS = 500;
    private static final int BAD_REQUEST_STATUS = 400;
    private static final int NOT_FOUND_STATUS = 404;
    private static final int METHOD_NOT_ALLOWED_STATUS = 405;
    private static final String GENERAL_ERROR_MESSAGE = "General error occurred";
    private static final String INVALID_ARGUMENT_MESSAGE = "Invalid argument";
    private static final String ITEM_NOT_FOUND_MESSAGE = "Item not found";
    private static final String METHOD_NOT_SUPPORTED_MESSAGE = "Method 'POST' is not supported";

    @InjectMocks
    private CustomizedResponseEntityExceptionHandler exceptionHandler;

    @Mock
    private WebRequest mockRequest;

    @Test
    void shouldHandleAllExceptions() {
        // Given
        Exception exception = new Exception(GENERAL_ERROR_MESSAGE);

        // When
        ResponseEntity<ErrorDetails> response = exceptionHandler.handleAllException(exception, mockRequest);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(INTERNAL_SERVER_ERROR_STATUS);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(GENERAL_ERROR_MESSAGE);
    }

    @Test
    void shouldHandleIllegalArgumentException() {
        // Given
        IllegalArgumentException exception = new IllegalArgumentException(INVALID_ARGUMENT_MESSAGE);

        // When
        ResponseEntity<ErrorDetails> response = exceptionHandler.handleIllegalArgumentException(exception, mockRequest);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(BAD_REQUEST_STATUS);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(INVALID_ARGUMENT_MESSAGE);
    }

    @Test
    void shouldHandleItemNotFoundException() {
        // Given
        ItemNotFoundException exception = new ItemNotFoundException(ITEM_NOT_FOUND_MESSAGE);

        // When
        ResponseEntity<ErrorDetails> response = exceptionHandler.handleInventoryNotFoundException(exception, mockRequest);

        // Then
        assertThat(response.getStatusCode().value()).isEqualTo(NOT_FOUND_STATUS);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo(ITEM_NOT_FOUND_MESSAGE);
    }

    @Test
    void shouldHandleHttpRequestMethodNotSupportedException() {
        // Given
        HttpRequestMethodNotSupportedException exception =
                new HttpRequestMethodNotSupportedException("POST", List.of("GET"));

        // When
        ResponseEntity<Object> response = exceptionHandler.handleHttpRequestMethodNotSupported(
                exception, null, null, mockRequest);

        // Then
        assert response != null;
        assertThat(response.getStatusCode().value()).isEqualTo(METHOD_NOT_ALLOWED_STATUS);
        assertThat(response.getBody()).isNotNull();
        ErrorDetails errorDetails = (ErrorDetails) response.getBody();
        assertThat(errorDetails.getMessage()).contains(METHOD_NOT_SUPPORTED_MESSAGE);
    }
}
