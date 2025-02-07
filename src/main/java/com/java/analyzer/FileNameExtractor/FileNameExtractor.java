package com.java.analyzer.FileNameExtractor;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileNameExtractor {
    public static String getFileName(String path) {
        try {
            // Пытаемся обработать как URL
            URI uri = new URI(path);
            String decodedPath = uri.getPath(); // Декодирует %20 в пробелы
            if (decodedPath.contains("/")) {
                return decodedPath.substring(decodedPath.lastIndexOf('/') + 1);
            }
            return decodedPath;
        } catch (URISyntaxException e) {
            // Если не URL, обрабатываем как локальный путь
            Path filePath = Paths.get(path);
            return filePath.getFileName().toString();
        }
    }
}