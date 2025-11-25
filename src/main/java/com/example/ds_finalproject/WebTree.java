package com.example.ds_finalproject;

public class WebTree {
    private WebNode root;

    public WebTree(WebNode root) {
        this.root = root;
    }

    public WebNode getRoot() {
        return root;
    }

    // 總分 = 根節點 + 子節點累積分數
    public double getTotalScore() {
        return root.getTotalScore();
    }
}
