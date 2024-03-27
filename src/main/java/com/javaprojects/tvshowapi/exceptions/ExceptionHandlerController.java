package com.javaprojects.tvshowapi.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ExceptionEntity> notFoundExceptionHandler(final NotFoundException e) {
        ExceptionEntity details = new ExceptionEntity(new Date(), e.getMessage());
        return new ResponseEntity<>(details, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<ExceptionEntity> badRequestExceptionHandler(final BadRequestException e) {
        ExceptionEntity details = new ExceptionEntity(new Date(), e.getMessage());
        return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServerException.class)
    public final ResponseEntity<ExceptionEntity> serverExceptionHandler(final ServerException e) {
        ExceptionEntity details = new ExceptionEntity(new Date(), e.getMessage());
        return new ResponseEntity<>(details, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
