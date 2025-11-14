package com.example.ds_finalproject;

public class SearchResult {
    private String title;
    private String link;
    private String snippet;

    public SearchResult(String title, String link, String snippet) {
        this.title = title;
        this.link = link;
        this.snippet = snippet;
    }

    public String getTitle() { return title; }
    public String getLink() { return link; }
    public String getSnippet() { return snippet; }
}


