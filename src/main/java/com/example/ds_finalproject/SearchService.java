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

    private static final String KEYWORD_FILE = "static/KeywordList.txt";
    private List<Keyword> keywords = readKeywords(KEYWORD_FILE);

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
        }   catch (Exception e) {
                System.err.println("Failed to fetch child pages for: " + parent.getUrl());
                parent.setChildren(new ArrayList<>()); // 失敗就設空 list
            }
    }


    private List<Keyword> readKeywords(String filename) {
    List<Keyword> keywords = new ArrayList<>();

    try (Scanner scanner = new Scanner(new java.io.File(filename), StandardCharsets.UTF_8)) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            // Skip empty lines
            if (line.isEmpty()) continue;

            // Remove [ and ]
            if (line.startsWith("[") && line.endsWith("]")) {
                line = line.substring(1, line.length() - 1).trim();
            }

            String[] parts = line.split(",");

            if (parts.length == 4) {
                String type = parts[0].trim();
                String subtype = parts[1].trim();
                String word = parts[2].trim();
                double weight = Double.parseDouble(parts[3].trim());

                keywords.add(new Keyword(type, subtype, word, weight));
                String debugMsg = String.format("Loaded keyword - Type: %s, Subtype: %s, Word: %s, Weight: %.2f",
                        type, subtype, word, weight);
                System.out.println(debugMsg);
            }
        }
    } catch (Exception e) {
        System.err.println("Failed to read keywords from file: " + filename);
    }

    return keywords;
}

}


    
    

