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

public class MarkdownFormat implements OutputFormat {
    private final String lineDivider = " |\n";
    private final String bytesSplitter = "b |\n\n";
    private final String columnSplitter = " | ";

    @Override
    public void reportingResources(Map<String, Integer> resources, StringBuilder report) {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(resources.entrySet());

        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        for (Map.Entry<String, Integer> entry : list) {
            report.append("| `").append(entry.getKey()).append("` | ").append(entry.getValue()).append("\n");
        }
    }

    @Override
    public void reportingCodes(Map<HttpCodes, Integer> codes, StringBuilder report) {
        List<Map.Entry<HttpCodes, Integer>> list = new ArrayList<>(codes.entrySet());

        list.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        for (Map.Entry<HttpCodes, Integer> entry : list) {
            report.append("| ").append(entry.getKey()).append(columnSplitter).
                    append(entry.getKey().getCode()).append(columnSplitter)
                    .append(entry.getValue()).append(lineDivider);
        }
    }

    @Override
    public void printInfo(LogStats stat, String fileName, String from, String to) {
        StringBuilder report = new StringBuilder();

        report.append("# Nginx Log Analysis Report\n\n");

        // First Table
        report.append("## Общая информация\n\n");
        report.append("| Метрика | Значение |\n");
        report.append("|:--------:|---------:|\n");
        report.append("| Файл(-ы) | `").append(fileName).append(lineDivider);
        report.append("| Начальная дата | ").append(from).append(lineDivider);
        report.append("| Конечная дата | ").append(to).append(lineDivider);
        report.append("| Количество запросов | ").append(stat.getCount()).append(lineDivider);
        report.append("| Средний размер ответа | ").append(stat.getAvgBytesSent()).append("b |\n");
        report.append("| 95p размера ответа | ").append(stat.getPercentile()).append(bytesSplitter);

        // Second Table
        report.append("## Запрашиваемые ресурсы\n\n");
        report.append("| Ресурс | Количество |\n");
        report.append("|:-------:|-----------:|\n");
        reportingResources(stat.getResources(), report);
        report.append("\n");

        //Third Table
        report.append("## Коды ответа\n\n");
        report.append("| Код | Имя | Количество |\n");
        report.append("|:----:|:----:|-----------:|\n");
        reportingCodes(stat.getCodes(), report);
        report.append("\n");

        try (OutputStreamWriter reportFile =
                     new OutputStreamWriter(new FileOutputStream(
                             "report.md"), StandardCharsets.UTF_8)) {
            reportFile.write(report.toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
