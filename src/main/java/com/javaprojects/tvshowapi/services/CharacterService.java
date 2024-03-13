package com.javaprojects.tvshowapi.services;

import com.javaprojects.tvshowapi.entities.Character;
import com.javaprojects.tvshowapi.repositories.CharacterRepository;
import com.javaprojects.tvshowapi.repositories.TVShowRepository;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@AllArgsConstructor
public class CharacterService {

    private final Logger logger;
    private final CharacterRepository characterRepository;
    private final TVShowRepository tvShowRepository;

    public List<Character> searchByName(String name) {
        if (name == null || name.equals("")) {
            logger.log(Level.INFO, "Return all characters");
            return characterRepository.findAll();
        } else {
            logger.log(Level.INFO, "Search is successful");
            return characterRepository.searchByName(name);
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
        if (characterRepository.findById(id).isPresent()) {
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

    public Character saveCharacter(Character character) {
        return characterRepository.save(character);
    }
}


