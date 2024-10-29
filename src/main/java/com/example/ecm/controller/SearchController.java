package com.example.ecm.controller;

import com.example.ecm.model.elasticsearch.DocumentElasticsearch;
import com.example.ecm.service.SearchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public List<DocumentElasticsearch> search(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "attributes", required = false) List<String> attributes,
            @RequestParam(value = "documentTypes", required = false) List<String> documentTypes
    ) throws Exception {
        return searchService.search(query, attributes, documentTypes);
    }

}
