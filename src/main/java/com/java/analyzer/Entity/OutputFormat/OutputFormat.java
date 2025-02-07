package com.java.analyzer.Entity.OutputFormat;

import com.java.analyzer.Entity.HttpCodes;
import com.java.analyzer.Entity.LogStats;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface OutputFormat {
    public void reportingResources(Map<String, Integer> resources, StringBuilder report);

    public void reportingCodes(Map<HttpCodes, Integer> codes, StringBuilder report);

    public void printInfo(LogStats stat, String fileName, String from, String to);
}
