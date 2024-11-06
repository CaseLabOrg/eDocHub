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
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
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
        document.setIsAlive(true);

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


    /**
     * Updates a document in the specified index by ID.
     *
     * @param documentId The ID of the document to update.
     * @param updatedFields A map of the fields and values to update.
     * @throws IOException If there's an error with the update.
     */
    public void updateDocument(String documentId, Map<String, Object> updatedFields) throws IOException {
        UpdateRequest updateRequest = new UpdateRequest(INDEX_DOCUMENTS, documentId);

        updateRequest.doc(updatedFields, XContentType.JSON);

        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);

        if (updateResponse.getResult().name().equalsIgnoreCase("UPDATED")) {
            log.info("Document " + documentId + " updated successfully.");
        } else {
            log.error("Document " + documentId + " was not updated. Status: " + updateResponse.getResult());
        }
    }


    /**
     * Searches for a document by documentVersionId and returns it as a DocumentElasticsearch object.
     *
     * @param documentVersionId The documentVersionId to search for.
     * @return A DocumentElasticsearch object matching the documentVersionId, or null if not found.
     * @throws IOException If there's an error during the search.
     */
    public DocumentElasticsearch searchByDocumentVersionId(long documentVersionId) throws IOException {

        SearchRequest searchRequest = new SearchRequest(INDEX_DOCUMENTS);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        searchSourceBuilder.query(QueryBuilders.termQuery("documentVersionId", documentVersionId));
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        SearchHit[] searchHits = searchResponse.getHits().getHits();
        if (searchHits.length > 0) {
            return mapper.readValue(searchHits[0].getSourceAsString(), DocumentElasticsearch.class);
        } else {
            return null;
        }
    }

    public void deleteByDocumentVersionId(long documentVersionId) throws IOException {
        DocumentElasticsearch documentElasticsearch = searchByDocumentVersionId(documentVersionId);
        updateDocument(documentElasticsearch.getId(), Map.of("isAlive", Boolean.FALSE));
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
            List<QueryBuilder> strictQueries = parseStrictTerms(searchString);
            for (QueryBuilder strictQuery : strictQueries) {
                request.must(strictQuery);  // Exact matches must be present
            }

            List<String> notInTerms = parseNotInQuery(searchString);
            for (String excludeTerm : notInTerms) {
                request.mustNot(QueryBuilders.multiMatchQuery(excludeTerm, "title", "description", "content"));
            }

            String[] words = searchString.split(" ");
            for (String word : words) {
                word = word.trim();
                if (!word.startsWith("\"") && !word.startsWith("!")) {
                    if (word.length() < 4) {
                        String wildcardQuery = "*" + word + "*";
                        request.should(QueryBuilders.wildcardQuery("title", wildcardQuery))
                                .should(QueryBuilders.wildcardQuery("description", wildcardQuery))
                                .should(QueryBuilders.wildcardQuery("content", wildcardQuery));
                    } else {
                        request.should(QueryBuilders.multiMatchQuery(word, "title", "description", "content")
                                .fuzziness(Fuzziness.AUTO));
                    }
                }
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
