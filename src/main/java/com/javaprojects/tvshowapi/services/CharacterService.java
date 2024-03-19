package com.javaprojects.tvshowapi.services;

import com.javaprojects.tvshowapi.cache.EntityCache;
import com.javaprojects.tvshowapi.entities.Character;
import com.javaprojects.tvshowapi.repositories.CharacterRepository;
import com.javaprojects.tvshowapi.repositories.TVShowRepository;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@AllArgsConstructor
public class CharacterService {

    private final Logger logger;
    private final CharacterRepository characterRepository;
    private final TVShowRepository tvShowRepository;

    private EntityCache<Integer, List<Character>> cache;

    public List<Character> searchByName(String name) {
        if (name == null || name.equals("")) {
            logger.log(Level.INFO, "Return all characters");
            return characterRepository.findAll();
        } else {
            int hashCode = Objects.hashCode(name);
            List<Character> characters = cache.get(hashCode);
            if (characters != null) {
                logger.log(Level.INFO, "Search in cache");
                return characters;
            } else {
                List<Character> result = new ArrayList<>(characterRepository.searchByName(name));
                logger.log(Level.INFO, "Search in database");
                cache.put(hashCode, result);
                return result;
            }
        }
    }

    public void insertCharacter(Long tvShowId, Character character) {
        if (tvShowRepository.findById(tvShowId).isPresent()) {
            character.setTvShow(tvShowRepository.findById(tvShowId).get());
            characterRepository.save(character);
            logger.log(Level.INFO, "Successfully added character " + character.getName());
        } else logger.log(Level.INFO, "Cannot insert. Character with such ID already exists!");
    }

    public void deleteCharacter(Long id) {
        Optional<Character> character = characterRepository.findById(id);
        if (character.isPresent()) {
            cache.remove(Objects.hashCode(character.get().getName()));
            characterRepository.deleteById(id);
            logger.log(Level.INFO, "Delete is successful");
        } else logger.log(Level.INFO, "Cannot delete. Character with such ID does not exist!");
    }

    public void updateCharacter(Character character) {
        if (characterRepository.findById(character.getId()).isPresent()) {
            character.setTvShow(characterRepository.findById(character.getId()).get().getTvShow());
            characterRepository.save(character);
            logger.log(Level.INFO, "Update is successful");
        } else logger.log(Level.INFO, "Cannot update. Character with such ID does not exist!");
    }

    public List<Character> searchByTVShowTitle(String title) {
        if (title == null || title.equals("")) {
            logger.log(Level.INFO, "No title provided");
            return new ArrayList<>();
        } else {
            logger.log(Level.INFO, "Searching");
            List<Character> characters = characterRepository.searchByTVShowTitle(title);
            if (!characters.isEmpty()) cache.put(Objects.hashCode(characters.get(0).getName()), characters);
            return characters;
        }
    }
}


