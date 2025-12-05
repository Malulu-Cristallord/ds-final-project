package com.example.ds_finalproject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;

import org.jsoup.Jsoup;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class SearchService {

    @Value("${google.api.key}")
    private String apiKey;

    @Value("${google.cse.id}")
    private String cseId;

    @PostConstruct
    public void checkKeys() {
        if (apiKey.isEmpty() || cseId.isEmpty()) {
            throw new IllegalStateException("Google API key or CSE ID not set!");
        }
    }

    private List<Keyword> keywords = KeywordList.KEYWORD_LIST;

    private static final int MAX_CHILD_PAGES = 1;

    public List<WebTree> searchGoogle(String query) throws Exception {
        // Build URL for Google CSE API
        String url = "https://www.googleapis.com/customsearch/v1?key=" + apiKey
                + "&cx=" + cseId
                + "&q=" + URLEncoder.encode(query, StandardCharsets.UTF_8)
                + "&num=10"
                + "&safe=off"
                + "&lr="
                + "&gl=tw";

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
        List<WebTree> resultTrees = new ArrayList<>();

        if (items != null) {
            for (Map<String, Object> item : items) {
                String link = (String) item.get("link");
                String snippet = (String) item.get("snippet");

                if (link == null || !link.startsWith("http")) continue;
                
                String content;
                try {
                    content = Jsoup.connect(link)
                                .userAgent("Mozilla/5.0")
                                .timeout(5000)
                                .get()
                                .text();
                } catch (Exception e) {
                    content = (String) item.get("snippet");
                }

                WebNode root = new WebNode(link, content);

                for (Keyword kw : keywords) {
                    kw.computeNode(root);
                }

                // addChildPages(root, MAX_CHILD_PAGES);

                WebTree tree = new WebTree(root);
                resultTrees.add(tree);
            }
        }
        
        resultTrees.sort((a,b) -> Double.compare(b.getTotalScore(), a.getTotalScore()));
        return resultTrees;
    }
    
    // private void addChildPages(WebNode parent, int maxChildren) {
    //     try {
    //         org.jsoup.nodes.Document doc = Jsoup.connect(parent.getUrl()).get();
    //         org.jsoup.select.Elements links = doc.select("a[href]");
    //         int count = 0;

    //         URI parentUri = new URI(parent.getUrl());
    //         String parentDomain = parentUri.getHost();

    //         for (org.jsoup.nodes.Element link : links) {
    //             if (count >= maxChildren) break;

    //             String childUrl = link.absUrl("href");
    //             if (childUrl.isEmpty()) continue;
    //             if (!childUrl.startsWith("http")) continue;
    //             if (childUrl.matches(".*(\\.xml|\\.gz|\\.pdf|\\.jpg|\\.png)$")) continue;

    //             URI childUri = new URI(childUrl);
    //             if (!childUri.getHost().endsWith(parentDomain)) continue;

    //             String content = Jsoup.connect(childUrl).get().text();
    //             WebNode child = new WebNode(childUrl, content);

    //             for (Keyword kw : keywords) {
    //                 kw.computeNode(child);
    //             }

    //             parent.addChild(child);
    //             count++;
    //         }
    //     } catch (Exception e) {
    //         System.err.println("Failed to fetch child pages for: " + parent.getUrl());
    //         parent.setChildren(new ArrayList<>());
    //     }
    // }
}