package com.example.ecm.service;

import com.example.ecm.dto.CreateDocumentRequest;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для работы с MinIO, реализующий загрузку, получение и удаление файлов.
 */
@RequiredArgsConstructor
@Service
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * Карта для сопоставления расширений файлов с их MIME-типами.
     */
    private static final Map<String, String> extensionToMimeType = new HashMap<>() {{
        put("pdf", "application/pdf");
        put("png", "image/png");
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("txt", "text/plain");
        put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }};

    /**
     * Загружает документ в MinIO.
     * Документ загружается в виде Base64-строки, которая декодируется перед отправкой.
     *
     * @param id идентификатор документа
     * @param createDocumentRequest запрос с данными документа
     * @return true, если загрузка прошла успешно, иначе false
     */
    public boolean addDocument(Long id, CreateDocumentRequest createDocumentRequest) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(createDocumentRequest.getBase64Content());
            String fileExtension = createDocumentRequest.getTitle().substring(createDocumentRequest.getTitle().lastIndexOf('.') + 1);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(id + "_" + createDocumentRequest.getTitle())
                            .stream(new ByteArrayInputStream(decodedBytes), decodedBytes.length, -1)
                            .contentType(extensionToMimeType.getOrDefault(fileExtension.toLowerCase(), "application/octet-stream"))
                            .build()
            );

            return true;
        } catch (MinioException e) {
            e.printStackTrace();
            return false;
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Получает документ из MinIO по имени и возвращает его содержимое в виде Base64-строки.
     *
     * @param name имя документа (идентификатор + название файла)
     * @return содержимое документа в формате Base64 или null в случае ошибки
     */
    public String getBase64DocumentByName(String name) {
        InputStream stream = null;
        try {
            stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(name)
                    .build());
            return Base64.getEncoder().encodeToString(stream.readAllBytes());
        } catch (MinioException e) {
            e.printStackTrace();
            return null;
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Удаляет документ из MinIO по его имени.
     *
     * @param name имя документа (идентификатор + название файла)
     */
    public void deleteDocumentByName(String name) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(name)
                    .build()
            );
        } catch (MinioException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
