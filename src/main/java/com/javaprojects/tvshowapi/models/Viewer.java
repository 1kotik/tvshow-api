package com.javaprojects.tvshowapi.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Viewer {
    private int id;
    private int age;
    private String name;
    private String country;
}
