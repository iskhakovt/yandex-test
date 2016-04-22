/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;

import android.os.AsyncTask;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Downloads and unpacks the MainActivity's artist JSON
 * Calls onLoaded on success and onNotLoaded on failure
 */
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

            // Download might finish when the application is in tray, so activity is null
            if (activity != null) {
                activity.onLoaded(attributes);
            }
        } catch (Exception e) {
            MainActivity activity = activityReference.get();

            // Download might finish when the application is in tray, so activity is null
            if (activity != null) {
                activity.onNotLoaded();
            }
        }
    }

    @Nullable
    private String downloadFile(String url) {
        // Prefer updated file

        byte[] data;

        if (BuildConfig.DEBUG) {
            // Do not use cached data
            data = CachingDownload.downloadNotCached(url);
        } else {
            data = CachingDownload.downloadTryUpdate(url);
        }

        if (data != null) {
            return new String(data);
        } else {
            return null;
        }
    }

    /**
     * Parse a JSON containing one ArtistItem entry
     * @param json JSON object to be unpacked
     * @return Artist Item
     */
    @Nullable
    private ArtistItem proceedEntry(JSONObject json) {
        try {
            String name = json.getString("name");

            ArrayList<String> genres = new ArrayList<>();
            for (int i = 0; i != json.getJSONArray("genres").length(); i++) {
                genres.add(json.getJSONArray("genres").getString(i));
            }

            int albums = Integer.parseInt(json.getString("albums"));
            int tracks = Integer.parseInt(json.getString("tracks"));
            String link = json.getString("link");
            String description = json.getString("description");
            String smallCoverUrl = json.getJSONObject("cover").getString("small");
            String bigCoverUrl = json.getJSONObject("cover").getString("big");

            return new ArtistItem(name, genres, albums, tracks, link, description, smallCoverUrl, bigCoverUrl);
        } catch (JSONException e) {
            return null;
        }
    }
}