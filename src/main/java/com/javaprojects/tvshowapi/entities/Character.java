package com.javaprojects.tvshowapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "character")
@Getter
@Setter
public class Character {
    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "feature")
    private String feature;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "tvshow_id")
    TVShow tvShow;
}
