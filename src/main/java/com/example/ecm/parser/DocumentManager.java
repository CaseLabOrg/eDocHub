package com.example.ecm.parser;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

@Slf4j
@Component
@Getter
public class DocumentManager {

    private final String PATH = "tmp/";

    public String getAbsolutePath() {
        return new File(PATH).getAbsolutePath();
    }

    public void saveFileFromBase64(String base64Content, String fileName) {
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
            fos.write(decodedBytes);
            log.info("Файл успешно сохранён: " + file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Ошибка при сохранении файла: " + e.getMessage());
        }
    }

    public Boolean deleteFile(String filename) {
        return new File(new File(PATH).getAbsolutePath(), filename).delete();
    }
}
