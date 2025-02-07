package com.java.analyzer.Entity.OutputFormat;

import com.java.analyzer.Entity.HttpCodes;
import com.java.analyzer.Entity.LogStats;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdocFormat implements OutputFormat {
    private final String tableBorder = "|===\n";
    private final String bytesSplitter = " b\n";
    public static final String LINE_DIVIDER = "\n";
    private final String columnSplitter = " | ";
    private final String HEADER_TWO_COL = "[cols=\"1,1\", options=\"header\"]\n";
    private final String HEADER_THREE_COL = "[cols=\"1,1,1\", options=\"header\"]\n";

    @Override
    public void reportingResources(Map<String, Integer> resources, StringBuilder report) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(resources.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        for (Map.Entry<String, Integer> entry : list) {
            report.append("| ")
                    .append(entry.getKey())
                    .append(" | ")
                    .append(entry.getValue())
                    .append("\n");
        }
    }

    @Override
    public void reportingCodes(Map<HttpCodes, Integer> codes, StringBuilder report) {
        List<Map.Entry<HttpCodes, Integer>> list = new ArrayList<>(codes.entrySet());
        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        for (Map.Entry<HttpCodes, Integer> entry : list) {
            HttpCodes code = entry.getKey();

            report.append("| ")
                    .append(code.getCode())
                    .append(" | ")
                    .append(code.name())
                    .append(" | ")
                    .append(entry.getValue())
                    .append("\n");
        }
    }

    @Override
    public void printInfo(LogStats stat, String fileName, String from, String to) {
        StringBuilder report = new StringBuilder();

        report.append("= Nginx Log Analysis Report\n\n");

        // First Table
        report.append("== Общая информация\n\n")
                .append(HEADER_TWO_COL)
                .append(tableBorder)
                .append("| Метрика | Значение\n")
                .append("| Файл(-ы) | `").append(fileName).append("`\n")
                .append("| Начальная дата | ").append(from).append("\n")
                .append("| Конечная дата | ").append(to).append("\n")
                .append("| Количество запросов | ").append(stat.getCount()).append("\n")
                .append("| Средний размер ответа | ").append(stat.getAvgBytesSent()).append(bytesSplitter)
                .append("| 95p размера ответа | ").append(stat.getPercentile()).append(bytesSplitter)
                .append(tableBorder).append("\n");

        // Second Table
        report.append("== Запрашиваемые ресурсы\n\n")
                .append(HEADER_TWO_COL)
                .append(tableBorder)
                .append("| Ресурс | Количество\n");
        reportingResources(stat.getResources(), report);
        report.append(tableBorder).append("\n");

        // Third Table
        report.append("== Коды ответа\n\n")
                .append(HEADER_THREE_COL)
                .append(tableBorder)
                .append("| Код | Имя | Количество\n");
        reportingCodes(stat.getCodes(), report);
        report.append(tableBorder).append("\n");

        try (OutputStreamWriter reportFile = new OutputStreamWriter(
                new FileOutputStream("report.adoc"),
                StandardCharsets.UTF_8)) {
            reportFile.write(report.toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}