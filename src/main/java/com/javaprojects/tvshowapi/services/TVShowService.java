package com.javaprojects.tvshowapi.services;

import com.javaprojects.tvshowapi.entities.TVShow;
import com.javaprojects.tvshowapi.repositories.TVShowRepository;
import jakarta.transaction.Transactional;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TVShowService {
    private static final String API_URL = "https://www.episodate.com/api/search";
    private final Logger logger;
    private final TVShowRepository tvShowRepository;

    public TVShowService(Logger logger, TVShowRepository tvShowRepository) {
        this.logger = logger;
        this.tvShowRepository = tvShowRepository;
    }


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
        logger.log(Level.INFO, "Search from API is successful");
        return results;
    }

    public List<TVShow> searchByTitle(String title) {
        if (title == null || title.equals("")) {
            logger.log(Level.INFO, "Return all viewers");
            return tvShowRepository.findAll();
        } else {
            logger.log(Level.INFO, "Search is successful");
            return tvShowRepository.searchByTitle(title);
        }
    }

    public void insertTVShow(TVShow tvShow) {
        if (tvShowRepository.findById(tvShow.getId()).isEmpty()) {
            tvShowRepository.save(tvShow);
            logger.log(Level.INFO, "Successfully added TV Show " + tvShow.getTitle());
        } else logger.log(Level.INFO, "TV Show with such ID already exists!");
    }
    @Transactional
    public void deleteTVShow(Long id) {
        if (tvShowRepository.findById(id).isPresent()) {
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
