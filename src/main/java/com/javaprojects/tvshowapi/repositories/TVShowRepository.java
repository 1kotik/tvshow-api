package com.javaprojects.tvshowapi.repositories;

import com.javaprojects.tvshowapi.entities.TVShow;
import com.javaprojects.tvshowapi.entities.Viewer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TVShowRepository extends JpaRepository<TVShow, Long> {
    @Query("SELECT tvShow FROM TVShow tvShow WHERE tvShow.title = ?1")
    List<TVShow> searchByTitle(String title);
}
