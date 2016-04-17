package com.example.iskhakovt.yandextest;


public class ArtistItem {
    private String name;
    private String genre;
    private int tracks;
    private int albums;
    private String description;
    private String link;
    private String smallCoverUrl;
    private String bigCoverUrl;

    public ArtistItem(String name, String genre, int tracks, int albums,
                      String description, String link, String smallCoverUrl, String bigCoverUrl) {
        this.name = name;
        this.genre = genre;
        this.tracks = tracks;
        this.albums = albums;
        this.description = description;
        this.link = link;
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

    public String getDescription() {
        return description;
    }

    public String getLink() {
        return link;
    }

    public String getSmallCoverUrl() {
        return smallCoverUrl;
    }

    public String getBigCoverUrl() {
        return bigCoverUrl;
    }
}
