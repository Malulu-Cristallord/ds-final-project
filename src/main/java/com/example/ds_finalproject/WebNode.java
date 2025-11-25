package com.example.ds_finalproject;

import java.util.ArrayList;
import java.util.List;

public class WebNode {
    private String url;
    private String content;
    private double score = 0.0;
    private List<WebNode> children = new ArrayList<>();

    public WebNode(String url, String content) {
        this.url = url;
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
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
