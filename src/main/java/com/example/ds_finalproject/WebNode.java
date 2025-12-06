package com.example.ds_finalproject;

import java.util.ArrayList;
import java.util.List;

public class WebNode {
    private String url;
    private String title;
    private String content;
    private String snippet;
    private double score = 0.0;
    private List<WebNode> children = new ArrayList<>();

    public WebNode(String url, String title, String content, String snippet) {
        this.url = url;
        this.title = title;
        this.content = content;
        this.snippet = snippet;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getSnippet() {
        return snippet;
    }

    public double getScore() {
        return score;
    }

    public List<WebNode> getChildren() {
        return children;
    }

    public void setChildren( List<WebNode> children) {
        this.children = children;
    }

    public void addChild(WebNode child) {
        children.add(child);
    }

    public void addScore(double s) {
        this.score += s;
    }

    // 計算子節點的累積分數
    public double getTotalScore() {
        double total = score;
        for (WebNode child : children) {
            total += child.getTotalScore();
        }
        return total;
    }
}
