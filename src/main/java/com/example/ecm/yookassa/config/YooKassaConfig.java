package com.example.ecm.yookassa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Configuration
public class YooKassaConfig {
    @Value("${yookassa.user}")
    private String YOOKASSA_USER;
    @Value("${yookassa.secret}")
    private String YOOKASSA_SECRET;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public HttpHeaders httpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(YOOKASSA_USER, YOOKASSA_SECRET);
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        return headers;
    }

}