package com.javaprojects.tvshowapi.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tvshow")
@Schema(name = "TV Show")
public class TVShow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "Title", example = "Supernatural")
    @Column(name = "title")
    private String title;

    @Schema(description = "Permanent link", example = "supernatural")
    @Column(name = "permalink")
    private String permalink;

    @Schema(description = "When the show started", example = "2004")
    @Column(name = "start_date")
    private String startDate;

    @Schema(description = "When the show ended", example = "01.01.2001")
    @Column(name = "end_date")
    private String endDate;

    @Schema(description = "Сountry", example = "USA")
    @Column(name = "country")
    private String country;

    @Schema(description = "Network where TV Show was airing", example = "Netflix")
    @Column(name = "network")
    private String network;

    @Schema(description = "Status of TV Show", example = "In development")
    @Column(name = "status")
    private String status;

    @Schema(description = "Link to the TV Show poster")
    @Column(name = "image_thumbnail_path")
    private String imageThumbnailPath;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, mappedBy = "tvShows")
    private Set<Viewer> viewers = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY, mappedBy = "tvShow")
    Set<Character> characters = new HashSet<>();
}
