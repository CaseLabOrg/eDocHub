package com.example.ecm.service;

import com.example.ecm.dto.requests.CreateDocumentVersionRequest;
import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
     * @return true, если загрузка прошла успешно, иначе false
     */
    public boolean addDocument(Long id, CreateDocumentVersionRequest request) {
        try {
            String base64Content = request.getBase64Content();

            byte[] fileBytes = new byte[0];
            String mimeType = "application/octet-stream";
            String filename = null;
            if (base64Content != null && !base64Content.isEmpty()) {
                String[] parts = base64Content.split(",");
                if (parts.length < 2) {
                    System.err.println("Некорректный формат Base64-строки, будет сохранена пустая строка.");
                } else {
                    fileBytes = Base64.getDecoder().decode(parts[1]);
                    mimeType = parseData(base64Content);
                    filename = parseFilename(base64Content);
                }
            } else {
                System.err.println("Base64-строка пуста, будет сохранена пустая строка.");
            }
            if(filename == null)
                filename = "Untitled";
            String fileKey = id + "_" + filename;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileKey)
                            .stream(new ByteArrayInputStream(fileBytes), fileBytes.length, -1)
                            .contentType(mimeType)
                            .build()
            );

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    /**
     * Извлекает документ из MinIO по имени и возвращает его содержимое в виде исходной Base64-строки,
     * сохраняя MIME-тип и префикс (если они были сохранены).
     *
     * @param name имя документа (идентификатор + название файла)
     * @return содержимое документа в исходном формате Base64 или null в случае ошибки
     */
    public String getBase64DocumentByName(String name) {
        try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(name)
                .build())) {


            byte[] fileBytes = stream.readAllBytes();


            String mimeType = minioClient.statObject(StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(name)
                            .build())
                    .contentType();

            String base64Content = Base64.getEncoder().encodeToString(fileBytes);
            return "filename:" + name.substring(name.indexOf('_') + 1) + ";data:" + mimeType + ";base64," + base64Content;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
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

    public String parseFilename(String base64Content) {
        String[] parts = base64Content.split(",");
        if (parts.length == 2) {
            String[] meta = parts[0].split(";");
            if (meta[0].contains("filename:")) {
                String filename = meta[0].substring(9);
                if(!filename.isEmpty())
                    return filename;
            }
        }
        return null;
    }

    public String parseData(String base64Content) {
        String[] parts = base64Content.split(",");
        if (parts.length == 2) {
            String[] meta = parts[0].split(";");
            if (meta[1].contains("data:")) {
                String data = meta[1].substring(5);
                if(!data.isEmpty())
                    return data;
            }
        }
        return null;
    }

    public String parseBase64(String base64Content) {
        String[] parts = base64Content.split(",");
        if (parts.length == 2) {
            return parts[1];
        }
        return null;
    }
}
