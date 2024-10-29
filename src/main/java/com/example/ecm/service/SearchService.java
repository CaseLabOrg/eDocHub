package com.example.ecm.service;

import com.example.ecm.mapper.DocumentMapper;
import com.example.ecm.model.elasticsearch.DocumentElasticsearch;
import com.example.ecm.parser.Base64Manager;
import com.example.ecm.parser.DocumentManager;
import com.example.ecm.parser.DocumentParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private static final String INDEX_DOCUMENTS = "documents";

    private final ObjectMapper mapper;
    private final RestHighLevelClient client;
    private final DocumentMapper documentMapper;
    private final UserService userService;
    private final DocumentTypeService documentTypeService;
    private final DocumentManager documentManager;
    private final Base64Manager base64Manager;
    private final DocumentParser documentParser;

    public void addIndexDocumentElasticsearch(DocumentElasticsearch document, String base64Content, Long documentVersionId) {

        document.setDocumentVersionId(documentVersionId);
        String fullFilename = document.getId() + "." + base64Manager.getFileExtensionFromBase64(base64Content);
        try {

            documentManager.saveFileFromBase64(base64Manager.removeMetadataPrefix(base64Content), fullFilename);
            String content = documentParser.parse(documentManager.getAbsolutePath() + "/" + fullFilename);
            document.setContent(content);

            IndexRequest indexRequest = new IndexRequest(INDEX_DOCUMENTS)
                    .id(document.getId())
                    .source(mapper.writeValueAsString(document), XContentType.JSON);

            client.index(indexRequest, RequestOptions.DEFAULT);

            log.info("Document to be indexed: " + mapper.writeValueAsString(document));
            log.info("Successfully indexed document with ID: " + document.getId());

        } catch (IOException e) {
            log.error("Failed to index document: " + e.getMessage());
        } catch (Exception ex) {
            log.error(ex.getMessage());
        } finally {
            if (documentManager.deleteFile(fullFilename))
                log.info("Success delete: " + fullFilename);
            else
                log.error("Not deleted: " + fullFilename);
        }

    }

    public List<DocumentElasticsearch> search(String searchString, List<String> attributes, List<String> documentTypes) throws Exception {

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

        if (searchString != null) {
            if (searchString.length() < 4) {
                String wildcardQuery = "*" + searchString + "*";
                request.should(QueryBuilders.wildcardQuery("title", wildcardQuery))
                        .should(QueryBuilders.wildcardQuery("description", wildcardQuery))
                        .should(QueryBuilders.wildcardQuery("content", wildcardQuery));
            } else {
                request.must(QueryBuilders.multiMatchQuery(searchString, "title", "description", "content")
                        .fuzziness(Fuzziness.AUTO));
            }
        } else {
            request.must(QueryBuilders.matchAllQuery());
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

//        List<Document> docs = new ArrayList<>();
//        for (DocumentElasticsearch de : documents) {
//            docs.add(
//                    documentMapper.toDocument(
//                            de,
//                            userService.findById(de.getUserId()).get(),
//                            documentTypeService.findById(de.getDocumentTypeId()).get(),
//                            de.getValues().entrySet()
//                    )
//            );
//        }

        return documents;
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
