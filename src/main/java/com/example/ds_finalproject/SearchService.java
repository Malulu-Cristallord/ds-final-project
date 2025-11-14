package com.example.ds_finalproject;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.web.client.RestTemplate;

public class SearchService {

    private static final String API_KEY = "YOUR_API_KEY";
    private static final String CX = "YOUR_SEARCH_ENGINE_ID";

    public List<SearchResult> search(String query) {

        String url = "https://www.googleapis.com/customsearch/v1?key=" + API_KEY +
                     "&cx=" + CX +
                     "&q=" + URLEncoder.encode(query, StandardCharsets.UTF_8) +
                     "&num=10"; // first 10 results

        RestTemplate rest = new RestTemplate();
        Map<String, Object> json = rest.getForObject(url, Map.class);

        List<SearchResult> results = new ArrayList<>();

        if (json == null || !json.containsKey("items"))
            return results;

        List<Map<String, Object>> items = (List<Map<String, Object>>) json.get("items");

        for (Map<String, Object> item : items) {
            String title = (String) item.get("title");
            String link = (String) item.get("link");
            String snippet = (String) item.get("snippet");
            results.add(new SearchResult(title, link, snippet));
        }

        return results;
    }
}
