package com.java.analyzer.StatGenerator;

import com.java.analyzer.Entity.HttpCodes;
import com.java.analyzer.Entity.LogStats;
import com.java.analyzer.Entity.NginxLog;

import java.util.List;

public class StatGenerator {

    public static LogStats generateStat(List<NginxLog> logs) {
        LogStats result = new LogStats();

        for (NginxLog log : logs) {
            result.setCount(result.getCount() + 1);

            String[] parts = log.getRequest().split(" ");
            String resource = parts[1];
            result.getResources().put(resource, result.getResources().getOrDefault(resource, 0) + 1);

            HttpCodes code = HttpCodes.fromCode(log.getStatus());
            result.getCodes().put(code, result.getCodes().getOrDefault(code, 0) + 1);

            result.setAvgBytesSent(result.getAvgBytesSent() + log.getBodyBytesSent());
        }

        result.setAvgBytesSent(result.getAvgBytesSent() / result.getCount());
        result.setPercentile((long) (0.95 * (result.getCount() - 1)));

        return result;
    }

}
