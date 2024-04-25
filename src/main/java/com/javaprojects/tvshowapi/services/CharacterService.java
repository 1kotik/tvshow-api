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
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

import static com.javaprojects.tvshowapi.utilities.Constants.SERVER_ERROR_MSG;
import static com.javaprojects.tvshowapi.utilities.Constants.INVALID_INFO_MSG;
import static com.javaprojects.tvshowapi.utilities.Constants.NOT_FOUND_MSG;

@AllArgsConstructor
public class CharacterService {

    private final CharacterRepository characterRepository;
    private final TVShowRepository tvShowRepository;

    private EntityCache<Integer, List<Character>> cache;

    public List<Character> getCharacters() {
        try {
            List<Character> result = characterRepository.findAll().stream()
                    .sorted(Comparator.comparing(Character::getId)).collect(Collectors.toList());
            if (!result.isEmpty()) {
                return result;
            }
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public List<Character> searchByName(final String name) {
        if (name == null || name.equals("")) {
            throw new BadRequestException(INVALID_INFO_MSG);
        } else {
                try {
                    List<Character> result = characterRepository.findAll().stream()
                            .filter(c -> c.getName().contains(name)).toList();
                    if (!result.isEmpty()) {
                        return result;
                    }
                } catch (Exception e) {
                    throw new ServerException(SERVER_ERROR_MSG);
                }
                throw new NotFoundException(NOT_FOUND_MSG);
        }
    }

    public ResponseEntity<String> insertCharacter(final String title, final Character character) {
        if (character.getName() == null || character.getName().equals("") || title == null || title.equals("")) {
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            if (!tvShowRepository.searchByTitle(title).isEmpty()) {
                TVShow tvShow = tvShowRepository.searchByTitle(title).get(0);
                character.setTvShow(tvShow);
                characterRepository.save(character);
                cache.remove(Objects.hashCode(character.getName()));
                return ResponseEntity.ok("Character is saved successfully");
            }
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public ResponseEntity<String> deleteCharacter(final Long id) {
        if (id == null) {
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            Optional<Character> character = characterRepository.findById(id);
            if (character.isPresent()) {
                cache.remove(Objects.hashCode(character.get().getName()));
                characterRepository.deleteById(id);
                return ResponseEntity.ok("Character is deleted successfully");
            }
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public ResponseEntity<String> updateCharacter(final Character character) {
        if (character.getName() == null || character.getName().equals("") || character.getId() == null) {
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            if (characterRepository.findById(character.getId()).isPresent()) {
                character.setTvShow(characterRepository.findById(character.getId()).get().getTvShow());
                cache.remove(Objects.hashCode(character.getName()));
                cache.remove(Objects.hashCode(characterRepository.findById(character.getId()).get().getName()));
                characterRepository.save(character);
                return ResponseEntity.ok("Character is updated successfully");
            }
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public List<Character> searchByTVShowTitle(final String title) {
        if (title == null || title.equals("")) {
            throw new BadRequestException(INVALID_INFO_MSG);
        } else {
            try {
                List<Character> result = characterRepository.findAll().stream()
                        .filter(c -> c.getTvShow().getTitle().equals(title)).toList();
                if (!result.isEmpty()) {
                    return result;
                }
            } catch (Exception e) {
                throw new ServerException(SERVER_ERROR_MSG);
            }
            throw new NotFoundException(NOT_FOUND_MSG);
        }
    }

    public Character findById(Long id) {
        if(characterRepository.findById(id).isPresent()) return characterRepository.findById(id).get();
        else return new Character();
    }
}
