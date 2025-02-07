package com.java.analyzer.LogsParser;

import com.java.analyzer.Entity.NginxLog;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogsParser {

    public static List<NginxLog> parseLogs(List<String> logs) {
        List<NginxLog> result = new ArrayList<>();
        String regex = "^([^ ]+) - ([^ ]+) \\[(.*?)\\] \"(.*?)\" (\\d+) (\\d+) \"(.*?)\" \"(.*?)\"$";
        Pattern pattern = Pattern.compile(regex);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

        for (String log : logs) {
            Matcher matcher = pattern.matcher(log);
            if (matcher.matches()) {
                try {
                    String remoteAddress = matcher.group(1);
                    String remoteUser = matcher.group(2);
                    Date timeLocal = dateFormat.parse(matcher.group(3));
                    String request = matcher.group(4);
                    int status = Integer.parseInt(matcher.group(5));
                    long bodyBytesSent = Long.parseLong(matcher.group(6));
                    String httpReferer = matcher.group(7);
                    String httpUserAgent = matcher.group(8);

                    NginxLog nginxLog = new NginxLog(
                            remoteAddress,
                            remoteUser,
                            timeLocal,
                            request,
                            status,
                            bodyBytesSent,
                            httpReferer,
                            httpUserAgent
                    );
                    result.add(nginxLog);
                } catch (ParseException e) {
                    System.err.println("Error parsing date in log: " + log);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing status or bytes in log: " + log);
                }
            } else {
                System.err.println("Log line does not match the expected format: " + log);
            }
        }
        return result;
    }
}