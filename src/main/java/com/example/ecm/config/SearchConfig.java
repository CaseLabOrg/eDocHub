package com.example.ecm.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SearchConfig {

    @Value("${elastic.username}")
    private String username;

    @Value("${elastic.password}")
    private String password;

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
