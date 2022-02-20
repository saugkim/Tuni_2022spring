package org.tuni.newsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonHandler {

    public static String TAG = "ZZ JsonHandler";

    public static NewsResult getNewsResult(String url) throws JSONException, IOException {
        JSONObject jsonObject = getJsonObjectFromNewsAPI(url);
        return getNewsResultFromJSONObject(jsonObject);
    }

    private static JSONObject getJsonObjectFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(bufferedReader);
            bufferedReader.close();
            return new JSONObject(jsonText);
        }
    }

    private static JSONObject getJsonObjectFromNewsAPI(String request) throws IOException, JSONException {

        HttpURLConnection conn=(HttpURLConnection)(new URL(request).openConnection());
        conn.setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
        conn.setRequestProperty("Accept","*/*");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");

        Log.d(TAG, "line 45 "+conn.getResponseCode());
        InputStream error = conn.getErrorStream();
        Log.d(TAG, "line 47 "+error);
        InputStream is = conn.getInputStream();
        Log.d(TAG, "line 49");

        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String jsonText = readAll(reader);
        reader.close();
        is.close();
        return new JSONObject(jsonText);
    }

    private static String readAll(BufferedReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ( (cp = reader.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static NewsResult getNewsResultFromJSONObject(JSONObject jsonObject) throws JSONException {
        String status = jsonObject.getString("status");
        JSONArray array = jsonObject.getJSONArray("articles");

        List<Article> lists = new ArrayList<>();
        for (int i=0;i<array.length();i++){
            String title = array.getJSONObject(i).getString("title");
            String desc = array.getJSONObject(i).getString("description");
            String url = array.getJSONObject(i).getString("url");
            String image = array.getJSONObject(i).getString("urlToImage");
            String date = array.getJSONObject(i).getString("publishedAt");

            Article article = new Article(title,desc, url, image, date);
            lists.add(article);
        }
        return new NewsResult(lists, status);
    }
}
