package com.example.ecm.init;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
            // Проверка существования бакета
            boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!isExist) {
                // Создание нового бакета, если он не существует
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
            }
        } catch (MinioException | InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            // Обработка возможных исключений
            System.err.println("Error occurred: " + e);
        }
    }
}
