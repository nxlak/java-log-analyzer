package com.java.analyzer.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class LogStats {
    private Integer count = 0;
    private Map<String, Integer> resources = new HashMap<>();
    private Map<HttpCodes, Integer> codes = new HashMap<>();
    private long avgBytesSent = 0;
    private long percentile = 0;
}
