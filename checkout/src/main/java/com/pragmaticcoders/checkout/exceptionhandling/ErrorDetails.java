package com.pragmaticcoders.checkout.exceptionhandling;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorDetails {

    private final int status;

    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    private final String message;
    private final String description;
}
