package com.example.ecm.parser;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class Base64Manager {

    private static final Map<String, String> extensionToMimeType = new HashMap<>() {{
        put("pdf", "application/pdf");
        put("png", "image/png");
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("txt", "text/plain");
        put("doc", "application/msword");
        put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        put("xls", "application/vnd.ms-excel");
    }};

    public static String extensionToMimeType(String mimeType) {
        return extensionToMimeType.getOrDefault(mimeType, "application/octet-stream");
    }
}
