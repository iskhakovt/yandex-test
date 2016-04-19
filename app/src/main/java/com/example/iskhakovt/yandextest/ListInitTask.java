/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ListInitTask extends AsyncTask<String, Void, String> {
    private final WeakReference<MainActivity> activityReference;

    public ListInitTask(MainActivity activity) {
        activityReference = new WeakReference<>(activity);
    }

    @Override
    protected String doInBackground(String... params) {
        return downloadFile(params[0]);
    }


    @Override
    protected void onPostExecute(String response) {
        if (isCancelled()) {
            return;
        }

        List<ArtistItem> attributes = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);

            for (int i = 0; i != jsonArray.length(); i++) {
                ArtistItem artistItem = this.proceedEntry(jsonArray.getJSONObject(i));
                if (artistItem != null) {
                    attributes.add(artistItem);
                }
            }

            MainActivity activity = activityReference.get();
            if (activity != null) {
                activity.loaded(attributes);
            }
        } catch (Exception e) {
            MainActivity activity = activityReference.get();
            if (activity != null) {
                activity.notLoaded();
            }
        }
    }

    @Nullable
    private String downloadFile(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            urlConnection.setInstanceFollowRedirects(true);

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } catch (IOException e) {
                return null;
            }

            return sb.toString();
        } catch (Exception e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    @Nullable
    private ArtistItem proceedEntry(JSONObject json) {
        try {
            String name = json.getString("name");

            ArrayList<String> genres = new ArrayList<>();
            for (int i = 0; i != json.getJSONArray("genres").length(); i++) {
                genres.add(json.getJSONArray("genres").getString(i));
            }

            int tracks = Integer.parseInt(json.getString("tracks"));
            int albums = Integer.parseInt(json.getString("albums"));
            String link = json.getString("link");
            String description = json.getString("description");
            String smallCoverUrl = json.getJSONObject("cover").getString("small");
            String bigCoverUrl = json.getJSONObject("cover").getString("big");

            return new ArtistItem(name, genres, tracks, albums, link, description, smallCoverUrl, bigCoverUrl);
        } catch (JSONException e) {
            return null;
        }
    }
}