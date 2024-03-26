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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "viewer")
@Getter
@Setter
@Schema(name = "Viewer")
public class Viewer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "Age", example = "19")
    @Column(name = "age")
    private int age;

    @Schema(description = "Name", example = "Alex")
    @Column(name = "name")
    private String name;

    @Schema(description = "Сountry", example = "Germany")
    @Column(name = "country")
    private String country;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "tvshow_viewer",
            joinColumns = @JoinColumn(name = "viewer_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tvshow_id", referencedColumnName = "id"))
    private Set<TVShow> tvShows = new HashSet<>();

}
