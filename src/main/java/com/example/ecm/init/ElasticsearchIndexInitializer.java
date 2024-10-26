package com.example.ecm.init;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class ElasticsearchIndexInitializer implements CommandLineRunner {

    private final RestHighLevelClient client;

    @Override
    public void run(String... args) throws Exception {
        createIndexIfNotExists("document_types", getDocumentTypesMapping());
        createIndexIfNotExists("attributes", getAttributesMapping());
        createIndexIfNotExists("documents", getDocumentsMapping());
        createIndexIfNotExists("document_versions", getDocumentVersionsMapping());
        createIndexIfNotExists("roles", getRolesMapping());
        createIndexIfNotExists("signatures", getSignaturesMapping());
        createIndexIfNotExists("users", getUsersMapping());
        createIndexIfNotExists("values", getValuesMapping());
    }

    private void createIndexIfNotExists(String indexName, String mapping) throws IOException {
        GetIndexRequest request = new GetIndexRequest(indexName);
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        if (!exists) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
            createIndexRequest.settings(Settings.builder()
                    .put("index.number_of_shards", 1)
                    .put("index.number_of_replicas", 1)
            );
            createIndexRequest.mapping(mapping, XContentType.JSON);
            CreateIndexResponse response = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            if (response.isAcknowledged()) {
                System.out.println("Index " + indexName + " created");
            }
        }
    }

    // Mappings for all the indices
    private String getDocumentTypesMapping() {
        return """
                {
                  "properties": {
                    "id": { "type": "long" },
                    "name": { "type": "text" },
                    "attributeIds": { "type": "long" }
                  }
                }""";
    }

    private String getAttributesMapping() {
        return """
                {
                  "properties": {
                    "id": { "type": "long" },
                    "name": { "type": "text" },
                    "required": { "type": "boolean" },
                    "documentTypeIds": { "type": "long" }
                  }
                }""";
    }

    private String getDocumentsMapping() {
        return """
                {
                  "properties": {
                    "id": { "type": "long" },
                    "userId": { "type": "long" },
                    "documentTypeId": { "type": "long" },
                    "documentVersions": {
                      "type": "nested",
                      "properties": {
                        "id": { "type": "long" },
                        "versionId": { "type": "long" },
                        "title": { "type": "text" },
                        "createdAt": { "type": "date", "format": "yyyy-MM-dd'T'HH:mm:ss.SSSZ" }
                      }
                    }
                  }
                }""";
    }

    private String getDocumentVersionsMapping() {
        return """
                {
                  "properties": {
                    "id": { "type": "long" },
                    "documentId": { "type": "long" },
                    "versionId": { "type": "long" },
                    "title": { "type": "text" },
                    "createdAt": { "type": "date", "format": "yyyy-MM-dd'T'HH:mm:ss.SSSZ" },
                    "values": {
                      "type": "nested",
                      "properties": {
                        "attributeId": { "type": "long" },
                        "value": { "type": "text" }
                      }
                    },
                    "signatures": {
                      "type": "nested",
                      "properties": {
                        "id": { "type": "long" },
                        "hash": { "type": "keyword" },
                        "userId": { "type": "long" }
                      }
                    }
                  }
                }""";
    }

    private String getRolesMapping() {
        return """
                {
                  "properties": {
                    "id": { "type": "long" },
                    "name": { "type": "text" },
                    "userIds": { "type": "long" }
                  }
                }""";
    }

    private String getSignaturesMapping() {
        return """
                {
                  "properties": {
                    "id": { "type": "long" },
                    "hash": { "type": "keyword" },
                    "userId": { "type": "long" },
                    "documentVersionId": { "type": "long" }
                  }
                }""";
    }

    private String getUsersMapping() {
        return """
                {
                  "properties": {
                    "id": { "type": "long" },
                    "name": { "type": "text" },
                    "surname": { "type": "text" },
                    "email": { "type": "keyword" },
                    "password": { "type": "keyword" },
                    "documentIds": { "type": "long" }
                  }
                }""";
    }

    private String getValuesMapping() {
        return """
                {
                  "properties": {
                    "id": { "type": "long" },
                    "attributeId": { "type": "long" },
                    "documentVersionId": { "type": "long" },
                    "value": { "type": "text" }
                  }
                }""";
    }
}
