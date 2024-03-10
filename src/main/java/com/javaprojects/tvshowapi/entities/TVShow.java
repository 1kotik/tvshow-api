package com.javaprojects.tvshowapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tvshow")
public class TVShow {
    @Id
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "permalink")
    private String permalink;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "end_date")
    private String endDate;

    @Column(name = "country")
    private String country;

    @Column(name = "network")
    private String network;

    @Column(name = "status")
    private String status;

    @Column(name = "image_thumbnail_path")
    private String imageThumbnailPath;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "tvShows")
    private Set<Viewer> viewers = new HashSet<>();
}
