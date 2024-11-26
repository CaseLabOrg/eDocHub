package com.example.ecm.init;

import com.example.ecm.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class ElasticsearchIndexInitializer implements CommandLineRunner {

    private final RestHighLevelClient client;
    private final SearchService searchService;


    @Override
    public void run(String... args) {
        try {
            createIndexIfNotExists("documents", getDocumentsMapping());
            searchService.synchronizeSearchEngine();
        } catch (IOException e) {
            System.err.println("Failed to initialize Elasticsearch index: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void createIndexIfNotExists(String indexName, String mapping) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        boolean exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);

        if (!exists) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);

            // Settings for analyzers and filters
            createIndexRequest.settings(Settings.builder()
                    .put("index.number_of_shards", 1)
                    .put("index.number_of_replicas", 1)
                    .put("analysis.filter.russian_stop.type", "stop")
                    .put("analysis.filter.russian_stop.stopwords", "_russian_")
                    .put("analysis.filter.english_stop.type", "stop")
                    .put("analysis.filter.english_stop.stopwords", "_english_")
                    .put("analysis.filter.russian_stemmer.type", "stemmer")
                    .put("analysis.filter.russian_stemmer.language", "russian")
                    .put("analysis.filter.english_stemmer.type", "stemmer")
                    .put("analysis.filter.english_stemmer.language", "english")
                    .put("analysis.analyzer.russian_analyzer.tokenizer", "standard")
                    .putList("analysis.analyzer.russian_analyzer.filter", "lowercase", "russian_stop", "russian_stemmer")
                    .put("analysis.analyzer.english_analyzer.tokenizer", "standard")
                    .putList("analysis.analyzer.english_analyzer.filter", "lowercase", "english_stop", "english_stemmer")
                    .put("analysis.analyzer.multilingual_analyzer.tokenizer", "standard")
                    .putList("analysis.analyzer.multilingual_analyzer.filter",
                            "lowercase", "russian_stop", "english_stop", "russian_stemmer", "english_stemmer")
            );

            // Set mapping for the index
            createIndexRequest.mapping(mapping, XContentType.JSON);
            CreateIndexResponse response = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);

            if (response.isAcknowledged()) {
                log.info("Index " + indexName + " created successfully.");
            } else {
                log.warn("Index creation for " + indexName + " was not acknowledged.");
            }
        } else {
            log.info("Index " + indexName + " already exists. No action needed.");
        }
    }

    // Mapping for the "documents" index
    private String getDocumentsMapping() {
        return """
                {
                  "properties": {
                    "id": { "type": "text" },
                    "documentVersionId": { "type": "long" },
                    "userId": { "type": "long" },
                    "documentTypeId": { "type": "long" },
                    "description": { "type": "text", "analyzer": "multilingual_analyzer" },
                    "content": { "type": "text", "analyzer": "multilingual_analyzer" },
                    "title": { "type": "text", "analyzer": "multilingual_analyzer" },
                    "values": { "type": "object", "dynamic": true },
                    "createdAt": { "type": "date", "format": "strict_date_time||epoch_millis" },
                    "isAlive": { "type": "boolean" }
                  }
                }""";
    }


}
