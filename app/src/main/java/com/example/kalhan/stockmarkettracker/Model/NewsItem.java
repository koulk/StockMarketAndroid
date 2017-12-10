package com.example.kalhan.stockmarkettracker.Model;

/**
 * Created by Kalhan on 11/18/2017.
 */

public class NewsItem {
    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getPubDate() {
        return pubDate;
    }


    public NewsItem(String author, String url, String title, String pubDate) {
        this.author = author;
        this.url = url;
        this.title = title;
        this.pubDate = pubDate;
    }

    private String author;
    private String url;
    private String title;
    private String pubDate;
}
