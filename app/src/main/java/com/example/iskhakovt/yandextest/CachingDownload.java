/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.support.annotation.Nullable;

import org.apache.commons.io.IOUtils;


/**
 * Downloader, saving files to cache
 */
public class CachingDownload {
    private static File cacheDir;

    /**
     * Init the cache directory (if not, files are not cached)
     * @param context application environment
     */
    public static void init(Context context) {
        cacheDir = context.getCacheDir();
    }

    /**
     * Download or load from cache a file if already downloaded
     * (make sure that the task is run by an AsyncTask)
     * @param url file url
     * @return binary file content
     */
    public static byte[] download(String url) {
        byte[] data = loadFile(url);
        if (data == null) {
            data = downloadUrl(url);
        }
        return data;
    }

    /**
     * Download a file without using cache
     * (make sure that the task is run by an AsyncTask)
     * @param url file url
     * @return binary file content
     */
    public static byte[] downloadNotCached(String url) {
        return downloadUrl(url);
    }

    /**
     * Download or load from cach a file e if there is no connection
     * (make sure that the task is run by an AsyncTask)
     * @param url file url
     * @return binary file content
     */
    public static byte[] downloadTryUpdate(String url) {
        byte[] data = downloadUrl(url);
        if (data == null) {
            data = loadFile(url);
        }
        return data;
    }

    @Nullable
    private static byte[] loadFile(String url) {
        try {
            String fileName = Integer.toString(url.hashCode());
            File file = new File(cacheDir, fileName);
            FileInputStream inputStream = new FileInputStream(file);
            return readStream(inputStream);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Download a file
     * (make sure that the task is run by an AsyncTask)
     * @param url file url
     * @return binary file content
     */
    @Nullable
    private static byte[] downloadUrl(String url) {
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
            if (inputStream != null) {
                byte[] data = readStream(inputStream);
                inputStream.close();

                if (data != null) {
                    saveFile(url, data);
                }
                return data;
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

    @Nullable
    private static byte[] readStream(InputStream stream) {
        try {
            return IOUtils.toByteArray(stream);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Save a binary file to cache
     * @param url unique file url (transformed to hashcode)
     * @param bytes file content
     */
    private static void saveFile(String url, byte[] bytes) {
        try {
            String fileName = Integer.toString(url.hashCode());
            File file = new File(cacheDir, fileName);
            OutputStream outputStream = new FileOutputStream(file);
            IOUtils.write(bytes, outputStream);
            outputStream.close();
        } catch (Exception e) { }
    }
}
