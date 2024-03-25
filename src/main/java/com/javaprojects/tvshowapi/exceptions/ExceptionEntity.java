package com.javaprojects.tvshowapi.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ExceptionEntity {
    Date timestamp;
    String message;
}
