package com.javaprojects.tvshowapi.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@AllArgsConstructor
@Getter
@Setter
public class ExceptionEntity {
    private Date timestamp;
    private String message;
}
