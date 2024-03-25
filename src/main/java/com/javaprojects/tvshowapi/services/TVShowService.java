package com.javaprojects.tvshowapi.services;

import com.javaprojects.tvshowapi.cache.EntityCache;
import com.javaprojects.tvshowapi.entities.Character;
import com.javaprojects.tvshowapi.entities.TVShow;
import com.javaprojects.tvshowapi.entities.Viewer;
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

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.javaprojects.tvshowapi.utilities.Constants.*;


@AllArgsConstructor
public class TVShowService {
    private static final String API_URL = "https://www.episodate.com/api/search";
    private final Logger logger;
    private final TVShowRepository tvShowRepository;
    private final CharacterRepository characterRepository;
    private EntityCache<Integer, List<TVShow>> cache;


    public List<TVShow> searchByTitleFromAPI(String title) throws IOException {
        List<TVShow> results = new ArrayList<>();
        if (title == null) title = "";
        String url = String.format("%s?q=%s", API_URL, URLEncoder.encode(title, StandardCharsets.UTF_8));
        try {

            // Выполнение GET запроса
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            // Проверка успешного выполнения запроса
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();

                // Парсинг JSON ответа
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONArray showsArray = jsonObject.getJSONArray("tv_shows");

                // Преобразование данных в объекты TVShow
                for (int i = 0; i < showsArray.length(); i++) {
                    JSONObject showObject = showsArray.getJSONObject(i);
                    TVShow tvShow = new TVShow();
                    tvShow.setId(showObject.optLong("id", i + 1L));
                    tvShow.setTitle(showObject.optString("name", null));
                    tvShow.setPermalink(showObject.optString("permalink", null));
                    tvShow.setStartDate(showObject.optString("start_date", null));
                    tvShow.setEndDate(showObject.optString("end_date", null));
                    tvShow.setCountry(showObject.optString("country", null));
                    tvShow.setNetwork(showObject.optString("network", null));
                    tvShow.setStatus(showObject.optString("status", null));
                    tvShow.setImageThumbnailPath(showObject.optString("image_thumbnail_path", null));
                    results.add(tvShow);
                }
            }
        } catch (IOException | JSONException e) {
            logger.log(Level.INFO, e.getMessage());
        }
        if (results.isEmpty()) {
            logger.log(Level.INFO, NOT_FOUND_MSG);
            throw new NotFoundException(NOT_FOUND_MSG);
        }
        try {
            tvShowRepository.saveAll(results);
            logger.log(Level.INFO, "Search from API is successful");
            return results;
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            throw new ServerException(SERVER_ERROR_MSG);
        }
    }

    public List<TVShow> getTVShows() {
        try {
            List<TVShow> result = tvShowRepository.findAll();
            if (!result.isEmpty()) return result;
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            throw new ServerException(SERVER_ERROR_MSG);
        }
        logger.log(Level.INFO, NOT_FOUND_MSG);
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public List<TVShow> searchByTitle(String title) {
        if (title == null || title.equals("")) {
            logger.log(Level.INFO, INVALID_INFO_MSG);
            throw new BadRequestException(INVALID_INFO_MSG);
        } else {
            int hashCode = Objects.hashCode(title);
            List<TVShow> tvShows = cache.get(hashCode);
            if (tvShows != null) {
                logger.log(Level.INFO, "Search in cache");
                return tvShows;
            } else {
                try {
                    List<TVShow> result = new ArrayList<>(tvShowRepository.searchByTitle(title));
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

    public void insertTVShow(TVShow tvShow) {
        if (tvShow.getTitle() == null || tvShow.getTitle().equals("")) {
            logger.log(Level.INFO, INVALID_INFO_MSG);
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        for (Character character : tvShow.getCharacters()) character.setTvShow(tvShow);
        for (Viewer viewer : tvShow.getViewers()) viewer.getTvShows().add(tvShow);
        try {
            tvShowRepository.save(tvShow);
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            throw new ServerException(SERVER_ERROR_MSG);
        }
        cache.remove(Objects.hashCode(tvShow.getTitle()));
        logger.log(Level.INFO, "Successfully added TV Show " + tvShow.getTitle());
    }

    @Transactional
    public void deleteTVShow(Long id) {
        if (id == null) {
            logger.log(Level.INFO, INVALID_INFO_MSG);
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            Optional<TVShow> tvShow = tvShowRepository.findById(id);
            if (tvShow.isPresent()) {
                for (Viewer viewer : tvShow.get().getViewers()) {
                    viewer.getTvShows().remove(tvShow.get());
                }
                characterRepository.deleteAll(tvShow.get().getCharacters());
                cache.remove(Objects.hashCode(tvShow.get().getTitle()));
                tvShowRepository.deleteById(id);
                logger.log(Level.INFO, "Delete is successful");
                return;
            }
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            throw new ServerException(SERVER_ERROR_MSG);
        }
        logger.log(Level.INFO, NOT_FOUND_MSG);
        throw new NotFoundException(NOT_FOUND_MSG);
    }

    public void updateTVShow(TVShow tvShow) {
        if (tvShow.getTitle() == null || tvShow.getTitle().equals("") || tvShow.getId() == null) {
            logger.log(Level.INFO, INVALID_INFO_MSG);
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            if (tvShowRepository.findById(tvShow.getId()).isPresent()) {
                cache.remove(Objects.hashCode(tvShow.getTitle()));
                cache.remove(Objects.hashCode(tvShowRepository.findById(tvShow.getId()).get().getTitle()));
                tvShowRepository.save(tvShow);
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

    public Set<Character> getCharacters(Long tvShowId) {
        if (tvShowId == null) {
            logger.log(Level.INFO, INVALID_INFO_MSG);
            throw new BadRequestException(INVALID_INFO_MSG);
        }
        try {
            Optional<TVShow> tvShow = tvShowRepository.findById(tvShowId);
            if (tvShow.isPresent() && !tvShow.get().getCharacters().isEmpty()) {
                logger.log(Level.INFO, "Returned characters");
                return tvShow.get().getCharacters();
            }
        } catch (Exception e) {
            logger.log(Level.INFO, e.getMessage());
            throw new ServerException(SERVER_ERROR_MSG);
        }
        logger.log(Level.INFO, NOT_FOUND_MSG);
        throw new NotFoundException(NOT_FOUND_MSG);
    }


    public void fillDB() {
        String baseURL = "https://www.episodate.com/api/most-popular?page=";
        List<TVShow> tvShows = new ArrayList<>();
        Long id = 1L;
        for (int page = 1; page < 1250; page++) {
            String paramURL = String.format("%s%d", baseURL, page);

            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(paramURL).build();
                Response response = client.newCall(request).execute();

                // Проверка успешного выполнения запроса
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();

                    // Парсинг JSON ответа
                    JSONObject jsonObject = new JSONObject(jsonResponse);
                    JSONArray showsArray = jsonObject.getJSONArray("tv_shows");

                    // Преобразование данных в объекты TVShow
                    for (int i = 0; i < showsArray.length(); i++) {
                        JSONObject showObject = showsArray.getJSONObject(i);
                        TVShow tvShow = new TVShow();
                        tvShow.setId(id);
                        tvShow.setTitle(showObject.optString("name", null));
                        tvShow.setPermalink(showObject.optString("permalink", null));
                        tvShow.setStartDate(showObject.optString("start_date", null));
                        tvShow.setEndDate(showObject.optString("end_date", null));
                        tvShow.setCountry(showObject.optString("country", null));
                        tvShow.setNetwork(showObject.optString("network", null));
                        tvShow.setStatus(showObject.optString("status", null));
                        tvShow.setImageThumbnailPath(showObject.optString("image_thumbnail_path", null));
                        tvShows.add(tvShow);
                        id++;
                    }
                    tvShowRepository.saveAll(tvShows);
                    logger.log(Level.INFO, "done");
                }
            } catch (IOException | JSONException e) {
                logger.log(Level.INFO, e.getMessage());
            }
        }
        logger.log(Level.INFO, "ALL DONE");
    }
}
