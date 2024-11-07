package com.example.ecm.parser;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Component
public class DocumentParser implements ContentParser {
    @Override
    public String parse(String filePath) throws Exception {

        InputStream is = new BufferedInputStream(new FileInputStream(filePath));

        StringBuilder text = new StringBuilder();
        FileMagic fileMagic = FileMagic.valueOf(is);

        if (filePath.endsWith(".xls") && fileMagic == FileMagic.OLE2) {
            // Для формата XLS
            try (HSSFWorkbook workbook = new HSSFWorkbook(is)) {
                for (Sheet sheet : workbook) {
                    for (Row row : sheet) {
                        for (Cell cell : row) {
                            text.append(cell.toString()).append(" ");
                        }
                    }
                }
            }
        } else if (filePath.endsWith(".xlsx") && fileMagic == FileMagic.OOXML) {
            // Для формата XLSX
            try (XSSFWorkbook workbook = new XSSFWorkbook(is)) {
                for (Sheet sheet : workbook) {
                    for (Row row : sheet) {
                        for (Cell cell : row) {
                            text.append(cell.toString()).append(" ");
                        }
                    }
                }
            }
        } else if (filePath.endsWith(".doc") && fileMagic == FileMagic.OLE2) {
            // Для формата DOC
            try (WordExtractor extractor = new WordExtractor(is)) {
                text.append(extractor.getText());
            }
        } else if (filePath.endsWith(".docx") && fileMagic == FileMagic.OOXML) {
            // Для формата DOCX
            try (XWPFDocument doc = new XWPFDocument(is);
                 XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
                 text.append(extractor.getText());
            }

        } else if (filePath.endsWith(".pdf")) {
            // Для формата PDF
            try (PDDocument document = PDDocument.load(new File(filePath))) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                text.append(pdfStripper.getText(document));
            }
        } else if (filePath.endsWith(".jpg") || filePath.endsWith(".png")) {
            // Для фото
            text.append(parseImg(filePath));
        } else {
            // Для всего остального просто читаем
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;

                while((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        is.close();

        return text.toString().replaceAll("\\s+", " ").trim();
    }


    private String parseImg(String filePath) {
        File imageFile = new File(filePath);
        if (!imageFile.exists()) {
            System.err.println("Error: File not found - " + filePath);
            return "";
        }

        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath(new File("src/main/resources/tessdata").getAbsolutePath());
        tesseract.setLanguage("rus+eng");
        String resultText = "";

        try {
            resultText = tesseract.doOCR(imageFile);
        } catch (TesseractException e) {
            System.err.println("Error during OCR: " + e.getMessage());
        }

        return resultText;
    }
}
