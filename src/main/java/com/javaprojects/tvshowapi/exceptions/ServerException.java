package com.javaprojects.tvshowapi.exceptions;

public class ServerException extends RuntimeException{
    public ServerException(String message){
        super(message);
    }
}
