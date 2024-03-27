package com.javaprojects.tvshowapi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "character")
@Getter
@Setter
@Schema(name = "Character")
public class Character {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID")
    private Long id;

    @Schema(description = "Name", example = "Dean Winchester")
    @Column(name = "name")
    private String name;

    @Schema(description = "Feature", example = "When he turned into vampire")
    @Column(name = "feature")
    private String feature;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tvshow_id")
    private TVShow tvShow;
}
