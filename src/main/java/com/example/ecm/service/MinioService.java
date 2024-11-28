package com.example.ecm.service;

import com.example.ecm.dto.requests.CreateDocumentVersionRequest;
import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
            // Разбиваем строку на части по разделителю `;`
            String base64Content = request.getBase64Content();
            String[] parts = base64Content.split(";");
            if (parts.length != 3) {
                System.err.println("Некорректный формат строки, ожидается формат 'filename:<name>;data:<mime-type>;base64,<content>'");
                return false;
            }

            // Извлекаем имя файла
            String filenamePart = parts[0];
            if (!filenamePart.startsWith("filename:")) {
                System.err.println("Некорректный формат имени файла.");
                return false;
            }
            String filename = filenamePart.substring(9);

            // Извлекаем MIME-тип
            String mimeTypePart = parts[1];
            if (!mimeTypePart.startsWith("data:")) {
                System.err.println("Некорректный формат MIME-типа.");
                return false;
            }
            String mimeType = mimeTypePart.substring(5);

            // Извлекаем Base64-данные
            String base64Part = parts[2];
            if (!base64Part.startsWith("base64,")) {
                System.err.println("Некорректный формат Base64-данных.");
                return false;
            }
            String base64Data = base64Part.substring(7);


            byte[] fileBytes = Base64.getDecoder().decode(base64Data);


            String fileKey = id + "_" + request.getTitle();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileKey)
                            .stream(new ByteArrayInputStream(fileBytes), fileBytes.length, -1)
                            .contentType(mimeType)
                            .userMetadata(Map.of("filename", filename)) // Сохраняем имя файла как метаданные
                            .build()
            );

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    public String getBase64DocumentByName(String fileKey) {
        try {
            // Получаем файл из MinIO
            try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileKey)
                    .build())) {


                byte[] fileBytes = stream.readAllBytes();

                // Получаем MIME-тип и имя файла из contentType
                String contentType = minioClient.statObject(StatObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileKey)
                                .build())
                        .contentType();

                // Извлекаем имя файла из метаданных
                String filename = minioClient.statObject(StatObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileKey)
                                .build())
                        .userMetadata()
                        .get("filename");


                String base64Content = Base64.getEncoder().encodeToString(fileBytes);

                // Возвращаем в формате filename:<name>;data:<mime-type>;base64,<content>
                return "filename:" + filename + ";data:" + contentType + ";base64," + base64Content;
            }
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
}
