package com.java.analyzer.LogsReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogsReader {
    private static List<String> readFromUrl(String path) throws IOException {
        URL url = new URL(path);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.toList());
        }
    }

    private static List<String> readFromLocalPath(String path) throws IOException {
        List<String> result = new ArrayList<>();
        PathDetails pathDetails = splitPathAndPattern(path);

        Path startDir = Paths.get(pathDetails.baseDir).toAbsolutePath();
        String globPattern = pathDetails.globPattern;

        boolean isLiteral = !globPattern.matches(".*[*?\\[].*");

        if (isLiteral) {
            Path targetFile = startDir.resolve(globPattern).normalize();
            if (Files.exists(targetFile) && Files.isRegularFile(targetFile)) {
                result.addAll(Files.readAllLines(targetFile, StandardCharsets.UTF_8));
            } else {
                throw new NoSuchFileException("File not found: " + targetFile);
            }
        } else {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
            try (Stream<Path> paths = Files.walk(startDir)) {
                paths.filter(Files::isRegularFile)
                        .filter(matcher::matches)
                        .forEach(file -> {
                            try {
                                result.addAll(Files.readAllLines(file, StandardCharsets.UTF_8));
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        });
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }
        }

        if (result.isEmpty()) {
            throw new NoSuchFileException("No files matching pattern: " + path);
        }
        return result;
    }

    private static PathDetails splitPathAndPattern(String rawPath) {
        String path = rawPath.replace('\\', '/');

        boolean hasWildcard = path.matches(".*[\\*\\?\\[].*");

        if (!hasWildcard) {
            Path p = Paths.get(path).toAbsolutePath();

            if (Files.exists(p)) {
                if (Files.isDirectory(p)) {
                    return new PathDetails(p.toString(), "*");
                } else {
                    Path parent = p.getParent() != null ? p.getParent() : Paths.get(".");
                    return new PathDetails(parent.toString(), p.getFileName().toString());
                }
            } else {
                int lastSlash = path.lastIndexOf('/');
                if (lastSlash == -1) {
                    return new PathDetails(".", path);
                } else {
                    String dir = path.substring(0, lastSlash);
                    String pattern = path.substring(lastSlash + 1);
                    return new PathDetails(dir, pattern);
                }
            }
        } else {
            int firstWildcard = -1;
            for (int i = 0; i < path.length(); i++) {
                char c = path.charAt(i);
                if (c == '*' || c == '?') {
                    firstWildcard = i;
                    break;
                }
            }
            if (firstWildcard == -1) {
                return new PathDetails(".", path);
            }
            int slashIndex = path.lastIndexOf('/', firstWildcard);
            if (slashIndex == -1) {
                return new PathDetails(".", path);
            } else {
                String dir = path.substring(0, slashIndex);
                String pattern = path.substring(slashIndex + 1);
                dir = dir.isEmpty() ? "." : dir;
                return new PathDetails(dir, pattern);
            }
        }
    }

    private static boolean isUrl(String path) {
        try {
            new URL(path);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static List<String> readLogs(String path) throws IOException {
        if (isUrl(path)) {
            return readFromUrl(path);
        } else {
            return readFromLocalPath(path);
        }
    }

    private static class PathDetails {
        String baseDir;
        String globPattern;

        PathDetails(String baseDir, String globPattern) {
            this.baseDir = baseDir;
            this.globPattern = globPattern;
        }
    }
}