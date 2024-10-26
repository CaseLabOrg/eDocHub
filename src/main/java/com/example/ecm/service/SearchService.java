package com.example.ecm.service;

import com.example.ecm.mapper.DocumentMapper;
import com.example.ecm.model.Document;
import com.example.ecm.model.elasticsearch.DocumentElasticsearch;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class SearchService {

    private final static String INDEX_DOCUMENTS = "documents";

    private final ObjectMapper mapper;
    private final RestHighLevelClient client;
    private final DocumentMapper documentMapper;
    private final UserService userService;
    private final DocumentTypeService documentTypeService;

    public SearchService(ObjectMapper mapper, RestHighLevelClient client, DocumentMapper documentMapper, UserService userService, DocumentTypeService documentTypeService) {
        this.mapper = mapper;
        this.client = client;
        this.documentMapper = documentMapper;
        this.userService = userService;
        this.documentTypeService = documentTypeService;
    }

    public void addIndexDocumentElasticsearch(DocumentElasticsearch document) {

        try {
            IndexRequest indexRequest = new IndexRequest(INDEX_DOCUMENTS)
                    .id(String.valueOf(document.getId()))
                    .source(mapper.writeValueAsString(document), XContentType.JSON);

            client.index(indexRequest, RequestOptions.DEFAULT);

            log.info("Document to be indexed: " + mapper.writeValueAsString(document));
            log.info("Successfully indexed document with ID: " + document.getId());

        } catch (IOException e) {
            log.error("Failed to index document: " + e.getMessage(), e);
        }
    }

    public List<Document> search(String searchString, List<String> attributes, List<String> documentTypes) throws Exception {

        SearchRequest searchRequest = new SearchRequest(INDEX_DOCUMENTS);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder request = QueryBuilders.boolQuery();

        if (attributes != null && !attributes.isEmpty()) {
            BoolQueryBuilder valuesQuery = QueryBuilders.boolQuery();

            for (String attribute : attributes) {
                valuesQuery.should(QueryBuilders.multiMatchQuery(attribute)
                        .field("values.*")
                        .fuzziness(Fuzziness.AUTO));
            }

            request.must(valuesQuery);
        }

        if (searchString != null && !searchString.isEmpty()) {
            request.must(QueryBuilders.multiMatchQuery(searchString, "title", "description").fuzziness(Fuzziness.AUTO));
        }

        searchSourceBuilder.query(request);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        List<DocumentElasticsearch> documents = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            DocumentElasticsearch document = mapper.convertValue(sourceAsMap, DocumentElasticsearch.class);
            documents.add(document);
        }

        List<Document> docs = new ArrayList<>();
        for (DocumentElasticsearch de : documents) {
            docs.add(
                    documentMapper.toDocument(
                            de,
                            userService.findById(de.getUserId()).get(),
                            documentTypeService.findById(de.getDocumentTypeId()).get(),
                            de.getValues().entrySet()
                    )
            );
        }

        return docs;
    }


    private List<QueryBuilder> parseStrictTerms(String searchString) {
        List<QueryBuilder> strictQueries = new ArrayList<>();
        String[] terms = searchString.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        for (String term : terms) {
            if (term.startsWith("\"") && term.endsWith("\"")) {
                String exactTerm = term.substring(1, term.length() - 1);
                strictQueries.add(QueryBuilders.multiMatchQuery(exactTerm, "title", "description")
                        .fuzziness(Fuzziness.ZERO));
            }
        }
        return strictQueries;
    }

    // Helper method to parse exclusion terms (terms prefixed with "!")
    private List<String> parseNotInQuery(String query) {
        List<String> notInQuery = new ArrayList<>();
        String regex = "!\"([^\"]+)\"|!([^\\s,]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(query);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                notInQuery.add(matcher.group(1));
            } else if (matcher.group(2) != null) {
                notInQuery.add(matcher.group(2));
            }
        }
        return notInQuery;
    }
}
