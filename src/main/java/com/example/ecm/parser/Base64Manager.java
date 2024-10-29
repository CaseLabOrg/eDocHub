package com.example.ecm.parser;

import org.springframework.stereotype.Component;

@Component
public class Base64Manager {

    private String getExtensionFromMimeType(String mimeType) {
        return switch (mimeType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            case "application/pdf" -> "pdf";
            case "text/plain" -> "txt";
            case "application/zip" -> "zip";
            case "application/docx" -> "docx";
            case "application/xlsx" -> "xlsx";
            default -> "bin";
        };
    }

    public String getFileExtensionFromBase64(String base64String) {
        if (base64String.startsWith("data:")) {
            String metadata = base64String.split(",")[0];
            String mimeType = metadata.split(";")[0].split(":")[1];

            return getExtensionFromMimeType(mimeType);
        } else {
            throw new IllegalArgumentException("Invalid Base64 string: missing data URI scheme");
        }
    }

    public String removeMetadataPrefix(String base64String) {
        if (base64String.contains(",")) {
            return base64String.substring(base64String.indexOf(",") + 1);
        }
        return base64String;
    }
}
