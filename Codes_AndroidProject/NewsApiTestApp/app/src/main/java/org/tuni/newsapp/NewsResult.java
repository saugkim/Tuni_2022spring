package org.tuni.newsapp;

import java.util.List;

public class NewsResult {
    private List<Article> articles;
    private String status;

    public NewsResult(List<Article> articles, String status) {
        this.articles = articles;
        this.status = status;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public String getStatus() {
        return status;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
