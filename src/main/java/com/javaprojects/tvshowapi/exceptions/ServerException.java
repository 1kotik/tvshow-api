package com.javaprojects.tvshowapi.exceptions;

public class ServerException extends RuntimeException {
    public ServerException(final String message) {
        super(message);
    }
}
