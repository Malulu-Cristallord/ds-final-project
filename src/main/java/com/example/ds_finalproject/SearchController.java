package com.example.ds_finalproject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
public class SearchController {

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

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) throws Exception {

        String url = "https://www.googleapis.com/customsearch/v1?key=" + apiKey
                     + "&cx=" + cseId
                     + "&q=" + URLEncoder.encode(query, StandardCharsets.UTF_8);

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        // Add the whole response to the model
        model.addAttribute("searchResults", response.get("items"));
        model.addAttribute("query", query);

        return "index";
    }
}

