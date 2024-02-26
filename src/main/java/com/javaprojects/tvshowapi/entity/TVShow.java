package com.javaprojects.tvshowapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TVShow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;
    private String title;
    private String permalink;
    private String startDate;
    private String endDate;
    private String country;
    private String network;
    private String status;
    private String imageThumbnailPath;
}
