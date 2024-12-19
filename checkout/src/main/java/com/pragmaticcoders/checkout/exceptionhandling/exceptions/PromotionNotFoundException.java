package com.pragmaticcoders.checkout.exceptionhandling.exceptions;

public class PromotionNotFoundException extends RuntimeException {

    public PromotionNotFoundException(String message) {
        super(message);
    }
}
