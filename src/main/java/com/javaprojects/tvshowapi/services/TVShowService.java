package com.javaprojects.tvshowapi.services;

import com.javaprojects.tvshowapi.cache.EntityCache;
import com.javaprojects.tvshowapi.entities.Character;
import com.javaprojects.tvshowapi.entities.TVShow;
import com.javaprojects.tvshowapi.entities.Viewer;
import com.javaprojects.tvshowapi.repositories.CharacterRepository;
import com.javaprojects.tvshowapi.repositories.TVShowRepository;
import com.javaprojects.tvshowapi.repositories.ViewerRepository;
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
        tvShowRepository.saveAll(results);
        logger.log(Level.INFO, "Search from API is successful");
        return results;
    }

    public List<TVShow> searchByTitle(String title) {
        if (title == null || title.equals("")) {
            logger.log(Level.INFO, "Return all TV Shows");
            return tvShowRepository.findAll();
        } else {
            int hashCode = Objects.hashCode(title);
            List<TVShow> tvShows = cache.get(hashCode);
            if (tvShows != null) {
                logger.log(Level.INFO, "Search in cache");
                return tvShows;
            } else {
                List<TVShow> result = new ArrayList<>(tvShowRepository.searchByTitle(title));
                logger.log(Level.INFO, "Search in database");
                cache.put(hashCode, result);
                return result;
            }
        }
    }

    public void insertTVShow(TVShow tvShow) {
        for (Character character : tvShow.getCharacters()) character.setTvShow(tvShow);
        for (Viewer viewer : tvShow.getViewers()) viewer.getTvShows().add(tvShow);
        tvShowRepository.save(tvShow);
        logger.log(Level.INFO, "Successfully added TV Show " + tvShow.getTitle());
    }

    @Transactional
    public void deleteTVShow(Long id) {
        Optional<TVShow> tvShow = tvShowRepository.findById(id);
        if (tvShow.isPresent()) {
            for (Viewer viewer : tvShow.get().getViewers()) {
                viewer.getTvShows().remove(tvShow.get());
            }
            characterRepository.deleteAll(tvShow.get().getCharacters());
            cache.remove(Objects.hashCode(tvShow.get().getTitle()));
            tvShowRepository.deleteById(id);
            logger.log(Level.INFO, "Delete is successful");
        } else logger.log(Level.INFO, "TV Show with such ID does not exist!");
    }

    public void updateTVShow(TVShow tvShow) {
        if (tvShowRepository.findById(tvShow.getId()).isPresent()) {
            tvShowRepository.save(tvShow);
            logger.log(Level.INFO, "Update is successful");
        } else logger.log(Level.INFO, "TV Show with such ID does not exist!");
    }

    public Set<Character> getCharacters(Long tvShowId) {
        Optional<TVShow> tvShow = tvShowRepository.findById(tvShowId);
        if (tvShow.isPresent()) {
            logger.log(Level.INFO, "Returned characters");
            return tvShow.get().getCharacters();
        } else {
            logger.log(Level.INFO, "Cannot get. TV Show with such ID does not exist!");
            return new HashSet<>();
        }
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
