package com.javaprojects.tvshowapi.services;

import com.javaprojects.tvshowapi.cache.EntityCache;
import com.javaprojects.tvshowapi.entities.Character;
import com.javaprojects.tvshowapi.entities.TVShow;
import com.javaprojects.tvshowapi.exceptions.BadRequestException;
import com.javaprojects.tvshowapi.exceptions.NotFoundException;
import com.javaprojects.tvshowapi.exceptions.ServerException;
import com.javaprojects.tvshowapi.repositories.CharacterRepository;
import com.javaprojects.tvshowapi.repositories.TVShowRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import java.util.*;

import static com.javaprojects.tvshowapi.utilities.Constants.SERVER_ERROR_MSG;
import static com.javaprojects.tvshowapi.utilities.Constants.INVALID_INFO_MSG;
import static com.javaprojects.tvshowapi.utilities.Constants.NOT_FOUND_MSG;


@AllArgsConstructor
public class TVShowService {
    private final TVShowRepository tvShowRepository;
    private final CharacterRepository characterRepository;
    private EntityCache<Integer, List<TVShow>> cache;

    public List<TVShow> getTVShows() {
        try {
            List<TVShow> result = tvShowRepository.findAll().stream()
                    .sorted((tv1, tv2) -> tv1.getId().compareTo(tv2.getId())).toList();
            if (!result.isEmpty()) {
                return result;
            }
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public List<TVShow> searchByTitle(final String title) {
        if (title == null || title.equals("")) {
            throw new BadRequestException(INVALID_INFO_MSG);
        } else {
            int hashCode = Objects.hashCode(title);
            List<TVShow> tvShows = cache.get(hashCode);
            if (tvShows != null) {
                return tvShows;
            } else {
                try {
                    List<TVShow> result = tvShowRepository.findAll().stream()
                            .filter(tv -> tv.getTitle().contains(title)).toList();
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

    public ResponseEntity<String> insertTVShow(final TVShow tvShow) {
        if (tvShow.getTitle() == null || tvShow.getTitle().equals("")) {
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        tvShow.getCharacters().forEach(c -> c.setTvShow(tvShow));
        tvShow.getViewers().forEach(v -> v.getTvShows().add(tvShow));
        try {
            tvShowRepository.save(tvShow);
            cache.remove(Objects.hashCode(tvShow.getTitle()));
            return ResponseEntity.ok("TV Show is inserted successfully");

        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
    }

    @Transactional
    public ResponseEntity<String> deleteTVShow(final Long id) {
        if (id == null) {
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            Optional<TVShow> tvShow = tvShowRepository.findById(id);
            if (tvShow.isPresent()) {
                tvShow.get().getViewers().forEach(v -> v.getTvShows().remove(tvShow.get()));
                characterRepository.deleteAll(tvShow.get().getCharacters());
                cache.remove(Objects.hashCode(tvShow.get().getTitle()));
                tvShowRepository.deleteById(id);
                return ResponseEntity.ok("TV Show is deleted successfully");
            }
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public ResponseEntity<String> updateTVShow(final TVShow tvShow) {
        if (tvShow.getTitle() == null || tvShow.getTitle().equals("") || tvShow.getId() == null) {
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        tvShow.getCharacters().forEach(c -> c.setTvShow(tvShow));
        tvShow.getViewers().forEach(v -> v.getTvShows().add(tvShow));
        try {
            if (tvShowRepository.findById(tvShow.getId()).isPresent()) {
                cache.remove(Objects.hashCode(tvShow.getTitle()));
                cache.remove(Objects.hashCode(tvShowRepository.findById(tvShow.getId()).get().getTitle()));
                tvShowRepository.save(tvShow);
                return ResponseEntity.ok("TV Show is updated successfully");
            }
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public List<Character> getCharacters(final Long tvShowId) {
        if (tvShowId == null) {
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            List<Character> result = characterRepository.findAll().stream().sorted(Comparator.comparing(Character::getId))
                    .filter(c -> c.getTvShow().getId().equals(tvShowId)).toList();
            if (!result.isEmpty()) {
                return result;
            }
        } catch (Exception e) {
            throw new ServerException(SERVER_ERROR_MSG);
        }
        throw new NotFoundException(NOT_FOUND_MSG);
    }
}
