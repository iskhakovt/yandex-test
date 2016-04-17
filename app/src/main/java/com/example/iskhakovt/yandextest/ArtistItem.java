package com.example.iskhakovt.yandextest;


public class ArtistItem {
    private String name;
    private String genre;
    private String description;
    private String smallImageUrl;
    private String bigImageUrl;

    public ArtistItem(String name, String genre, String description, String smallImageUrl, String bigImageUrl) {
        this.name = name;
        this.genre = genre;
        this.description = description;
        this.smallImageUrl = smallImageUrl;
        this.bigImageUrl = bigImageUrl;
    }

    public String getName() {
        return name;
    }

    public String getGenre() {
        return genre;
    }

    public String getDescription() {
        return description;
    }

    public String getSmallImageUrl() {
        return smallImageUrl;
    }

    public String getBigImageUrl() {
        return bigImageUrl;
    }
}
