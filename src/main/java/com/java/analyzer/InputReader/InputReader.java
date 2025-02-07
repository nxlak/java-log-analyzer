package com.java.analyzer.InputReader;
import org.apache.commons.cli.*;

public class InputReader {

    public static CommandLine commandsReader(String[] args) {
        CommandLine cmd;

        Options options = new Options();

        // Path
        Option pathOpt = Option.builder().longOpt("path").desc("Path to files").hasArg().required().build();
        options.addOption(pathOpt);

        // Time
        Option startOpt = Option.builder().longOpt("from").desc("Start time").hasArg().build();
        options.addOption(startOpt);

        Option endOpt = Option.builder().longOpt("to").desc("End time").hasArg().build();
        options.addOption(endOpt);

        // Format
        Option formatOpt = Option.builder().longOpt("format").desc("Format of output").hasArg().build();
        options.addOption(formatOpt);

        CommandLineParser parser = new DefaultParser();

        try {
            cmd = parser.parse(options, args);
            return cmd;
        } catch (ParseException e) {
            System.exit(1);
            return null;
        }

    }
}
