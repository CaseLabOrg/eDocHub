package com.example.ecm.config;

import com.example.ecm.model.elasticsearch.DocumentElasticsearch;
import jakarta.annotation.PostConstruct;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

@Configuration
public class SearchConfig {

    @Value("${elastic.hostname}")
    private String hostname;

    @Value("${elastic.port}")
    private int port;

    @Value("${elastic.scheme}")
    private String scheme;

    @Bean
    public RestHighLevelClient searchClient() {
//        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
//
//        return new RestHighLevelClient(
//                RestClient.builder(new HttpHost(hostname, port, scheme))
//                        .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider))
//        );

        return new RestHighLevelClient(RestClient.builder(new HttpHost(hostname, port, scheme)));
    }

}
