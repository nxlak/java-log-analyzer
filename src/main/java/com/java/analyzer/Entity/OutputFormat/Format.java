package com.java.analyzer.Entity.OutputFormat;

public class Format {

    public static OutputFormat getFormat(String format) {
        switch (format) {
            case "adoc":
                return new AdocFormat();
            default:
                return new MarkdownFormat();
        }
    }
}
