package com.example.ds_finalproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, @RequestParam(name="region", required=false) String region, Model model) throws Exception {

        // 呼叫 SearchService 拿搜尋結果
        List<WebTree> searchResults = searchService.searchGoogle(query, region);

        // 加入 Model
        model.addAttribute("searchResults", searchResults);
        model.addAttribute("query", query);
        model.addAttribute("region", region);

        return "search";
    }
}
