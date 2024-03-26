package com.javaprojects.tvshowapi.services;

import com.javaprojects.tvshowapi.cache.EntityCache;
import com.javaprojects.tvshowapi.entities.Character;
import com.javaprojects.tvshowapi.entities.TVShow;
import com.javaprojects.tvshowapi.exceptions.BadRequestException;
import com.javaprojects.tvshowapi.exceptions.NotFoundException;
import com.javaprojects.tvshowapi.exceptions.ServerException;
import com.javaprojects.tvshowapi.repositories.CharacterRepository;
import com.javaprojects.tvshowapi.repositories.TVShowRepository;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.javaprojects.tvshowapi.utilities.Constants.*;

@AllArgsConstructor
public class CharacterService {

    private final Logger logger;
    private final CharacterRepository characterRepository;
    private final TVShowRepository tvShowRepository;

    private EntityCache<Integer, List<Character>> cache;

    public List<Character> getCharacters() {
        try {
            List<Character> result = characterRepository.findAll();
            if (!result.isEmpty()) return result;
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            throw new ServerException(SERVER_ERROR_MSG);
        }
        logger.log(Level.INFO, NOT_FOUND_MSG);
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public List<Character> searchByName(String name) {
        if (name == null || name.equals("")) {
            logger.log(Level.INFO, INVALID_INFO_MSG);
            throw new BadRequestException(INVALID_INFO_MSG);
        } else {
            int hashCode = Objects.hashCode(name);
            List<Character> characters = cache.get(hashCode);
            if (characters != null) {
                logger.log(Level.INFO, "Search in cache");
                return characters;
            } else {
                try {
                    List<Character> result = new ArrayList<>(characterRepository.searchByName(name));
                    if (!result.isEmpty()) {
                        logger.log(Level.INFO, "Search in database");
                        cache.put(hashCode, result);
                        return result;
                    }
                } catch (Exception e) {
                    logger.log(Level.INFO, e.getMessage());
                    throw new ServerException(SERVER_ERROR_MSG);
                }
                logger.log(Level.INFO, NOT_FOUND_MSG);
                throw new NotFoundException(NOT_FOUND_MSG);
            }
        }
    }

    public void insertCharacter(Long tvShowId, Character character) {
        if (character.getName() == null || character.getName().equals("") || tvShowId == null) {
            logger.log(Level.INFO, INVALID_INFO_MSG);
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            if (tvShowRepository.findById(tvShowId).isPresent()) {
                character.setTvShow(tvShowRepository.findById(tvShowId).get());
                characterRepository.save(character);
                cache.remove(Objects.hashCode(character.getName()));
                logger.log(Level.INFO, "Successfully added character " + character.getName());
                return;
            }
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            throw new ServerException(SERVER_ERROR_MSG);
        }
        logger.log(Level.INFO, NOT_FOUND_MSG);
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public void deleteCharacter(Long id) {
        if (id == null) {
            logger.log(Level.INFO, INVALID_INFO_MSG);
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        Optional<Character> character = characterRepository.findById(id);
        try {
            if (character.isPresent()) {
                cache.remove(Objects.hashCode(character.get().getName()));
                characterRepository.deleteById(id);
                logger.log(Level.INFO, "Character Delete is successful");
                return;
            }
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            throw new ServerException(SERVER_ERROR_MSG);
        }
        logger.log(Level.INFO, NOT_FOUND_MSG);
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public void updateCharacter(Character character) {
        if (character.getName() == null || character.getName().equals("") || character.getId() == null) {
            logger.log(Level.INFO, INVALID_INFO_MSG);
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            if (characterRepository.findById(character.getId()).isPresent()) {
                character.setTvShow(characterRepository.findById(character.getId()).get().getTvShow());
                cache.remove(Objects.hashCode(character.getName()));
                cache.remove(Objects.hashCode(characterRepository.findById(character.getId()).get().getName()));
                characterRepository.save(character);
                logger.log(Level.INFO, "Update is successful");
                return;
            }
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            throw new ServerException(SERVER_ERROR_MSG);
        }
        logger.log(Level.INFO, NOT_FOUND_MSG);
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public List<Character> searchByTVShowTitle(String title) {
        if (title == null || title.equals("")) {
            logger.log(Level.INFO, INVALID_INFO_MSG);
            throw new BadRequestException(INVALID_INFO_MSG);
        } else {
            try {
                List<Character> result = characterRepository.searchByTVShowTitle(title);
                if (!result.isEmpty()) {
                    logger.log(Level.INFO, "Searching");
                    return result;
                }
            } catch (Exception e) {
                logger.log(Level.INFO, e.getMessage());
                throw new ServerException(SERVER_ERROR_MSG);
            }
            logger.log(Level.INFO, NOT_FOUND_MSG);
            throw new NotFoundException(NOT_FOUND_MSG);
        }
    }
}


