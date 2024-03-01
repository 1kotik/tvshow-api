package com.javaprojects.tvshowapi.dao;

import com.javaprojects.tvshowapi.entity.TVShow;
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

public class TVShowDAO {
    private static final String API_URL = "https://www.episodate.com/api/search";
    private Logger logger;

    public TVShowDAO(Logger logger) {
        this.logger = logger;
    }


    public List<TVShow> searchByTitle(String title) throws IOException {
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
                    tvShow.setId(showObject.optInt("id", i + 1));
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
        logger.log(Level.INFO, "Success");
        return results;
    }
}
