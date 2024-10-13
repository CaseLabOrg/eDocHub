package com.example.ecm.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки MinIO клиента.
 * MinIO — это высокопроизводительное объектное хранилище, совместимое с S3 API.
 */
@Configuration
public class MinioConfig {

    /**
     * URL MinIO сервера, полученный из файла конфигурации.
     * Это может быть IP-адрес сервера MinIO или доменное имя.
     */
    @Value("${minio.url}")
    private String minioUrl;

    /**
     * Access Key для доступа к MinIO, полученный из файла конфигурации.
     * Этот ключ используется для аутентификации в MinIO.
     */
    @Value("${minio.accessKey}")
    private String accessKey;

    /**
     * Secret Key для доступа к MinIO, полученный из файла конфигурации.
     * Используется вместе с Access Key для аутентификации.
     */
    @Value("${minio.secretKey}")
    private String secretKey;

    /**
     * Создает и настраивает объект MinioClient, используя данные из конфигурации.
     * MinioClient используется для взаимодействия с MinIO сервером.
     *
     * @return объект {@link MinioClient}, настроенный для работы с указанным MinIO сервером.
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
    }
}
