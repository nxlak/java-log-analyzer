package com.java.analyzer.LogsFilter;

import com.java.analyzer.Entity.NginxLog;
import java.text.ParseException;

//import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class LogsFilter {

    public static List<NginxLog> filterLogs(List<NginxLog> logs, String from, String to) throws ParseException {
        List<NginxLog> result = new ArrayList<>();
        //SimpleDateFormat logDateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        LocalDate fromDate = null;
        LocalDate toDate = null;

        if (!from.isEmpty()) {
            fromDate = LocalDate.parse(from, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
        }

        if (!to.isEmpty()) {
            toDate = LocalDate.parse(to, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH));
        }

        for (NginxLog log : logs) {
            LocalDate logDate = log.getTimeLocal().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            boolean include = true;

            if (fromDate != null && logDate.isBefore(fromDate)) {
                include = false;
            }
            if (include && toDate != null && logDate.isAfter(toDate)) {
                include = false;
            }

            if (include) {
                result.add(log);
            }
        }

        return result;
    }
}
