package com.java.analyzer;
import com.java.analyzer.Entity.LogStats;
import com.java.analyzer.Entity.NginxLog;
import com.java.analyzer.Entity.OutputFormat.OutputFormat;
import org.apache.commons.cli.CommandLine;

import java.text.ParseException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.java.analyzer.Entity.OutputFormat.Format.getFormat;
import static com.java.analyzer.FileNameExtractor.FileNameExtractor.getFileName;
import static com.java.analyzer.InputReader.InputReader.commandsReader;
import static com.java.analyzer.LogsFilter.LogsFilter.filterLogs;
import static com.java.analyzer.LogsReader.LogsReader.readLogs;
import static com.java.analyzer.LogsParser.LogsParser.parseLogs;
import static com.java.analyzer.StatGenerator.StatGenerator.generateStat;

public class Main {
    public static void main(String[] args) {

        CommandLine cmd = commandsReader(args);

        String path = cmd.getOptionValue("path");
        List<String> logs = new ArrayList<>();
        try {
            logs = readLogs(path);
        }
        catch (IOException e) {
            System.exit(1);
        }

        List<NginxLog> parsedLogs = parseLogs(logs);

        String from = cmd.getOptionValue("from", "");
        String to = cmd.getOptionValue("to", "");

        if (!from.isEmpty() || !to.isEmpty()) {
            try {
                parsedLogs = filterLogs(parsedLogs, from, to);
            } catch (ParseException e) {
                System.err.println("Error parsing date in filter: " + e.getMessage());
                System.exit(1);
            }
        }

        LogStats stats = generateStat(parsedLogs);

        OutputFormat format = getFormat(cmd.getOptionValue("format"));
        String fileName = getFileName(cmd.getOptionValue("path"));
        format.printInfo(stats, fileName, from, to);
    }
}