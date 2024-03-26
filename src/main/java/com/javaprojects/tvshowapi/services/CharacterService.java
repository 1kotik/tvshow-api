package com.javaprojects.tvshowapi.services;

import com.javaprojects.tvshowapi.cache.EntityCache;
import com.javaprojects.tvshowapi.entities.Character;
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

import static com.javaprojects.tvshowapi.utilities.Constants.*;

@AllArgsConstructor
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final TVShowRepository tvShowRepository;

    private EntityCache<Integer, List<Character>> cache;

    public List<Character> getCharacters() {
        try {
            List<Character> result = characterRepository.findAll();
            if (!result.isEmpty()) return result;
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public List<Character> searchByName(String name) {
        if (name == null || name.equals("")) {
            throw new BadRequestException(INVALID_INFO_MSG);
        } else {
            int hashCode = Objects.hashCode(name);
            List<Character> characters = cache.get(hashCode);
            if (characters != null) {
                return characters;
            } else {
                try {
                    List<Character> result = new ArrayList<>(characterRepository.searchByName(name));
                    if (!result.isEmpty()) {
                        cache.put(hashCode, result);
                        return result;
                    }
                } catch (Exception e) {
                    throw new ServerException(SERVER_ERROR_MSG);
                }
                throw new NotFoundException(NOT_FOUND_MSG);
            }
        }
    }

    public void insertCharacter(Long tvShowId, Character character) {
        if (character.getName() == null || character.getName().equals("") || tvShowId == null) {
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            if (tvShowRepository.findById(tvShowId).isPresent()) {
                character.setTvShow(tvShowRepository.findById(tvShowId).get());
                characterRepository.save(character);
                cache.remove(Objects.hashCode(character.getName()));
                return;
            }
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public void deleteCharacter(Long id) {
        if (id == null) {
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        Optional<Character> character = characterRepository.findById(id);
        try {
            if (character.isPresent()) {
                cache.remove(Objects.hashCode(character.get().getName()));
                characterRepository.deleteById(id);
                return;
            }
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public void updateCharacter(Character character) {
        if (character.getName() == null || character.getName().equals("") || character.getId() == null) {
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            if (characterRepository.findById(character.getId()).isPresent()) {
                character.setTvShow(characterRepository.findById(character.getId()).get().getTvShow());
                cache.remove(Objects.hashCode(character.getName()));
                cache.remove(Objects.hashCode(characterRepository.findById(character.getId()).get().getName()));
                characterRepository.save(character);
                return;
            }
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public List<Character> searchByTVShowTitle(String title) {
        if (title == null || title.equals("")) {
            throw new BadRequestException(INVALID_INFO_MSG);
        } else {
            try {
                List<Character> result = characterRepository.searchByTVShowTitle(title);
                if (!result.isEmpty()) {
                    return result;
                }
            } catch (Exception e) {
                throw new ServerException(SERVER_ERROR_MSG);
            }
            throw new NotFoundException(NOT_FOUND_MSG);
        }
    }
}


