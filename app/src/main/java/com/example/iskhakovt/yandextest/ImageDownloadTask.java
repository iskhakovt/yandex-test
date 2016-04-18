/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.ImageView;


public class ImageDownloadTask extends AsyncTask<String, Void, Pair<Bitmap, String>> {
    private final WeakReference<ImageView> imageViewReference;

    public ImageDownloadTask(ImageView imageView) {
        imageViewReference = new WeakReference<>(imageView);
    }

    @Override
    protected Pair<Bitmap, String> doInBackground(String... params) {
        return downloadBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Pair<Bitmap, String> response) {
        if (isCancelled()) {
            response = null;
        }

        ImageView imageView = imageViewReference.get();
        if (imageView != null) {
            if (response != null) {
                if (imageView.getContentDescription().equals(response.second)) {
                    imageView.setImageBitmap(response.first);
                }
            }
        }
    }

    private Pair<Bitmap, String> downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();

            int statusCode = urlConnection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                return null;
            }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                return Pair.create(BitmapFactory.decodeStream(inputStream), url);
            }
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
}
