package com.example.ds_finalproject;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;

import java.net.URI;
import java.util.*;

@Service
public class SearchService {

    @Value("${serp.api.key}")
    private String apiKey;

    @PostConstruct
    public void checkKeys() {
        if (apiKey.isEmpty()) {
            throw new IllegalStateException("Google API key or CSE ID not set!");
        }
    }

    private List<Keyword> keywords = KeywordList.KEYWORD_LIST;

    public List<WebTree> searchGoogle(String query, String region) throws Exception {
        String extraSearchTerm="";
        if (region == null) {
            extraSearchTerm = "台灣南部旅遊";
        } else {
            switch (region) {
                case "nonSpecific":
                    extraSearchTerm = "台灣南部旅遊";
                    break;
                case "Chiayi":
                    extraSearchTerm = "嘉義旅遊";
                    break;
                case "Tainan":
                    extraSearchTerm = "台南旅遊";
                    break;
                case "Kaohsiung":
                    extraSearchTerm = "高雄旅遊";
                    break;
                case "Pingtung":
                    extraSearchTerm = "屏東旅遊";
                    break;
                default:
                    extraSearchTerm = "台灣南部旅遊";
                    break;
            }
        }
        
        String url = "https://serpapi.com/search.json"
           + "?q=" + query + " " + extraSearchTerm
           + "&hl=zh-TW"
           + "&gl=tw"
           + "&num=10"
           + "&api_key=" + apiKey;

        System.out.println("Request URL: " + url);

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("organic_results");
        List<WebTree> resultTrees = new ArrayList<>();

        if (items != null) {
            for (Map<String, Object> item : items) {
                String link = (String) item.get("link");
                String title = (String) item.get("title");
                String snippet = (String) item.get("snippet");

                if (link == null || !link.startsWith("http")) continue;
                if (link.endsWith(".xml.gz") || link.endsWith(".pdf") || link.contains("apple.com")) continue;

                if (snippet == null) snippet = "";

                WebNode root = new WebNode(link, title, snippet, snippet);

                for (Keyword kw : keywords) {
                    kw.computeNode(root);
                }

                addChildPages(root, MAX_CHILD_PAGES);

                WebTree tree = new WebTree(root);
                resultTrees.add(tree);

            }
        }
            


        resultTrees.sort((a, b) -> Double.compare(b.getTotalScore(), a.getTotalScore()));
        return resultTrees;
    }
    private static final int MAX_CHILD_PAGES = 3;
private static final int TIMEOUT = 5000;

    
 private int addChildPages(WebNode parent, int maxChildren) {
    int count = 0;

    try {
        org.jsoup.nodes.Document doc = Jsoup
                .connect(parent.getUrl())
                .timeout(5000)
                .userAgent("Mozilla/5.0")
                .get();

        org.jsoup.select.Elements links = doc.select("a[href]");

        URI parentUri = new URI(parent.getUrl());
        String parentDomain = parentUri.getHost();

        for (org.jsoup.nodes.Element link : links) {
            if (count >= maxChildren) break;

            String childUrl = link.absUrl("href");

            if (childUrl.isEmpty()) continue;
            if (!childUrl.startsWith("http")) continue;
            if (childUrl.matches(".*(\\.xml|\\.gz|\\.pdf|\\.jpg|\\.png)$")) continue;

            URI childUri = new URI(childUrl);
            if (childUri.getHost() == null) continue;
            if (!childUri.getHost().endsWith(parentDomain)) continue;

            org.jsoup.nodes.Document childDoc = Jsoup
                    .connect(childUrl)
                    .timeout(5000)
                    .userAgent("Mozilla/5.0")
                    .get();

            WebNode child = new WebNode(
                    childUrl,
                    childDoc.title(),
                    childDoc.text(),
                    childDoc.text()
            );

            for (Keyword kw : keywords) {
                kw.computeNode(child);
                //System.out.println("Child Page Keyword Score Computed: " + child.getTitle() + " (" + kw.getWord() + ") Total score : " + child.getScore());    
            }

            parent.addChild(child);
            parent.setHasChildren(true);
            count++;

            System.out.println("[ChildPage] " + parent.getUrl() + " -> " + childUrl);
        }

        if (count == 0) {
            System.out.println("[ChildPage] No child pages found for: " + parent.getUrl());
        }

    } catch (Exception e) {
        System.err.println("[ChildPage] ERROR for: " + parent.getUrl());
        
    }

    return count;
}


}