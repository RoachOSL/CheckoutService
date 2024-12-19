package com.pragmaticcoders.checkout.exceptionhandling.exceptions;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(String message) {
        super(message);
    }
}
