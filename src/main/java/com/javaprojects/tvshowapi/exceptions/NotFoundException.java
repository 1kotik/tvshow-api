package com.javaprojects.tvshowapi.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException(final String message) {
        super(message);
    }
}
