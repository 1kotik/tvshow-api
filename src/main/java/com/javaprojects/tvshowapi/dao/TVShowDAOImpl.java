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

public class TVShowDAOImpl implements TVShowDAO {
    private static final String API_URL = "https://www.episodate.com/api/search";

    @Override
    public List<TVShow> searchByTitle(String title) throws IOException {
        List<TVShow> results = new ArrayList<>();

        String url = String.format("%s?q=%s",API_URL ,URLEncoder.encode(title, StandardCharsets.UTF_8));
        try {

            // Выполнение HTTP GET запроса
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Response response = client.newCall(request).execute();

            // Проверка успешного выполнения запроса
            if (response.isSuccessful()) {
                String jsonResponse = response.body().string();

                // Парсинг JSON ответа
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONArray showsArray = jsonObject.getJSONArray("tv_shows");

                // Преобразование данных в объекты Series
                for (int i = 0; i < showsArray.length(); i++) {
                    JSONObject showObject = showsArray.getJSONObject(i);
                    TVShow tvShow = new TVShow();
                    tvShow.setId(showObject.optInt("id",i+1));                  //optInt для случая, если у ключа нет данных (null)
                    tvShow.setTitle(showObject.optString("name", null));        //optString аналогично
                    tvShow.setPermalink(showObject.optString("permalink", null));
                    tvShow.setStartDate(showObject.optString("start_date",null));
                    tvShow.setEndDate(showObject.optString("end_date",null));
                    tvShow.setCountry(showObject.optString("country", null));
                    tvShow.setNetwork(showObject.optString("network", null));
                    tvShow.setStatus(showObject.optString("status", null));
                    tvShow.setImageThumbnailPath(showObject.optString("image_thumbnail_path", null));
                    results.add(tvShow);
                }
            } else {
                System.err.println("Error: HTTP request failed with code " + response.code());
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return results;
    }
}
