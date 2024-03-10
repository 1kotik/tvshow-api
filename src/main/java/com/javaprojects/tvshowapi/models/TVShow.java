package com.javaprojects.tvshowapi.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private List<Integer> viewersId;
}
