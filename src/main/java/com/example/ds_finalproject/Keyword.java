package com.example.ds_finalproject;

public class Keyword {
    public final String type, subtype, word;
    public final double weight;

    public Keyword(String type, String subtype, String word, double weight) {
        this.type = type;
        this.subtype = subtype;
        this.word = word;
        this.weight = weight;
    }

    public String getWord() {
        return word;
    }
    public String getType() {
        return type;
    }
    public String getSubtype() {
        return subtype;
    }
    public double getWeight() {
        return weight;
    }

    // 計算這個 keyword 在 WebNode 的內容出現次數並加分
    public void computeNode(WebNode node) {
        if (node == null || node.getContent() == null) return;
        String contentLower = node.getContent().toLowerCase();
        String wordLower = word.toLowerCase();
        int count = 0;
        int index = 0;
        while ((index = contentLower.indexOf(wordLower, index)) != -1) {
            count++;
            index += wordLower.length();
        }
        node.addScore(count * weight);
    }
}
