package com.example.ecm.init;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Инициализатор MinIO, который запускается при старте приложения.
 * Проверяет наличие указанного бакета (корзины) в MinIO, и если его нет, создает новый бакет.
 */
@RequiredArgsConstructor
@Component
public class MinioInitializer implements CommandLineRunner {

    /**
     * Клиент для взаимодействия с MinIO, автоматически внедрен через конструктор.
     */
    private final MinioClient minioClient;

    /**
     * Название бакета, полученное из конфигурации.
     */
    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * Метод, который запускается автоматически при старте приложения.
     * Проверяет, существует ли бакет с именем, указанным в конфигурации, и если нет, создает его.
     *
     * @param args аргументы командной строки, если таковые переданы
     */
    @Override
    public void run(String... args) {
        try {
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());

                byte[] decodedBytes = Base64.getDecoder().decode("SGVsbG8sIFdvcmxkIQ=="); // Hello, World txt file

                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object("1_hw")
                                .stream(new ByteArrayInputStream(decodedBytes), decodedBytes.length, -1)
                                .contentType("text/plain")
                                .build()
                ); // поменять при изменении миграций
            }
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            System.err.println("Error occurred: " + e);
        }
    }
}
