/*
 * Copyright (c) Timur Iskhakov.
 * Distributed under the terms of the MIT License.
 */


package com.example.iskhakovt.yandextest;


import java.io.Serializable;

public class ArtistItem implements Serializable {
    private String name;
    private String genre;
    private int tracks;
    private int albums;

    private String link;
    private String description;
    private String smallCoverUrl;
    private String bigCoverUrl;

    public ArtistItem(String name, String genre, int tracks, int albums,
                      String link, String description, String smallCoverUrl, String bigCoverUrl) {
        this.name = name;
        this.genre = genre;
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

    public String getGenre() {
        return genre;
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
}
