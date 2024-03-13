package com.javaprojects.tvshowapi.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "viewer")
@Getter
@Setter
public class Viewer {
    @Id
    private Long id;

    @Column(name = "age")
    private int age;

    @Column(name = "name")
    private String name;

    @Column(name = "country")
    private String country;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "tvshow_viewer",
            joinColumns = @JoinColumn(name = "viewer_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "tvshow_id", referencedColumnName = "id"))
    private Set<TVShow> tvShows = new HashSet<>();

}
