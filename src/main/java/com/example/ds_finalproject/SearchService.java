package com.example.ds_finalproject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.jsoup.Jsoup;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class SearchService {

    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.cse.id}")
    private String cseId;

    private List<Keyword> keywords = List.of(
            new Keyword("嘉義", 10),
            new Keyword("旅遊", 8),
            new Keyword("南部", 5)
    );

    private static final int MAX_CHILD_PAGES = 3;

    public List<WebTree> searchGoogle(String query) throws Exception {
        String url = "https://www.googleapis.com/customsearch/v1?key=" + apiKey
                + "&cx=" + cseId
                + "&q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

        RestTemplate restTemplate = new RestTemplate();
        Map<String,Object> response = restTemplate.getForObject(url, Map.class);
        List<Map<String,Object>> items = (List<Map<String,Object>>) response.get("items");

        List<WebTree> resultTrees = new ArrayList<>();
        if (items != null) {
            for (Map<String,Object> item : items) {
                String link = (String) item.get("link");
                String snippet = (String) item.get("snippet");

                WebNode root = new WebNode(link, snippet);

                for (Keyword kw : keywords) {
                    kw.computeNode(root);
                }

                addChildPages(root, MAX_CHILD_PAGES);

                WebTree tree = new WebTree(root);
                resultTrees.add(tree);
            }
        }

        resultTrees.sort((a,b) -> Double.compare(b.getTotalScore(), a.getTotalScore()));
        return resultTrees;
    }
    private void addChildPages(WebNode parent, int maxChildren) {
    try {
        // 你的爬子頁面邏輯
        org.jsoup.nodes.Document doc = Jsoup.connect(parent.getUrl()).get();
            org.jsoup.select.Elements links = doc.select("a[href]");
            int count = 0;
            for (org.jsoup.nodes.Element link : links) {
                if (count >= maxChildren) break;
                String childUrl = link.absUrl("href");
                if (childUrl.isEmpty()) continue;

                String content = Jsoup.connect(childUrl).get().text();
                WebNode child = new WebNode(childUrl, content);

                for (Keyword kw : keywords) {
                    kw.computeNode(child);
                }

                parent.addChild(child);
                count++;}
    } catch (Exception e) {
        System.err.println("Failed to fetch child pages for: " + parent.getUrl());
        parent.setChildren(new ArrayList<>()); // 失敗就設空 list
    }
}
    }


    
    

