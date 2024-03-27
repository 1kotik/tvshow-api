package com.javaprojects.tvshowapi.repositories;

import com.javaprojects.tvshowapi.entities.Character;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CharacterRepository extends JpaRepository<Character, Long> {
    @Query("SELECT character FROM Character character WHERE character.name =:name")
    List<Character> searchByName(String name);

    @Query("SELECT character FROM Character character WHERE character.tvShow.title =:title")
    List<Character> searchByTVShowTitle(String title);
}
