package org.tuni.newsapp;

public class Article {

    private String title;
    private String description;
    private String url;
    private String imageUrl;
    private String date;

    public Article() {

    }

    public Article(String title, String description, String url, String imageUrl, String date) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.imageUrl = imageUrl;
        this.date = date;
    }

    public String getDate() {
        return date;
    }
    public String getDescription() {
        return description;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public String getTitle() {
        return title;
    }
    public String getUrl() {
        return url;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
