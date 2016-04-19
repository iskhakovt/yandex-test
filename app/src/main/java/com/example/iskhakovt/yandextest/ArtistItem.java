/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;


import android.text.TextUtils;

import java.io.Serializable;
import java.util.List;


/**
 * Artist entry
 */
public class ArtistItem implements Serializable {
    private String name;
    private List<String> genres;
    private int tracks;
    private int albums;

    private String link;
    private String description;
    private String smallCoverUrl;
    private String bigCoverUrl;

    public ArtistItem(String name, List<String> genres, int tracks, int albums,
                      String link, String description, String smallCoverUrl, String bigCoverUrl) {
        this.name = name;
        this.genres = genres;
        this.tracks = tracks;
        this.albums = albums;
        this.link = link;
        this.description = description;
        this.smallCoverUrl = smallCoverUrl;
        this.bigCoverUrl = bigCoverUrl;
    }

    public String getName() {
        return name;
    }

    public List<String> getGenres() {
        return genres;
    }

    /**
     * Get a string of genres
     * @param separator A separator for entries
     */
    public String getGenreString(String separator) {
        return TextUtils.join(separator, genres);
    }

    public int getTracks() {
        return tracks;
    }

    public int getAlbums() {
        return albums;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getSmallCoverUrl() {
        return smallCoverUrl;
    }

    public String getBigCoverUrl() {
        return bigCoverUrl;
    }

    /**
     * Check if the artist has a genre
     */
    public Boolean hasGenre(String genre) {
        return genres.indexOf(genre) != -1;
    }
}
