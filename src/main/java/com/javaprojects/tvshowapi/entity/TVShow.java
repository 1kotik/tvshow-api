package com.javaprojects.tvshowapi.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TVShow {
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
