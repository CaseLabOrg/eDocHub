package com.example.ecm.init;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ElasticsearchIndexInitializer implements CommandLineRunner {

    private final RestHighLevelClient client;

    @Autowired
    public ElasticsearchIndexInitializer(RestHighLevelClient client) {
        this.client = client;
    }

    @Override
    public void run(String... args) throws Exception {
        createIndexIfNotExists("documents", getDocumentsMapping());
    }

    private void createIndexIfNotExists(String indexName, String mapping) throws IOException {
        GetIndexRequest request = new GetIndexRequest(indexName);
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        if (!exists) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);

            // Настройки для анализаторов
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

            // Устанавливаем маппинг
            createIndexRequest.mapping(mapping, XContentType.JSON);
            CreateIndexResponse response = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);

            if (response.isAcknowledged()) {
                System.out.println("Index " + indexName + " created");
            }
        }
    }

    // Маппинг для индекса "documents" с анализаторами
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
                    "createdAt": { "type": "date", "format": "strict_date_time||epoch_millis" }
                  }
                }""";
    }
}
