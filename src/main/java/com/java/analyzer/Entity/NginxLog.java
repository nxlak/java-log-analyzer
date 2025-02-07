package com.java.analyzer.Entity;

import lombok.*;

import java.util.Date;

@Getter
@ToString
@AllArgsConstructor
public class NginxLog {
    private String remoteAddress;
    private String remoteUser;
    private Date timeLocal;
    private String request;
    private int status;
    private long bodyBytesSent;
    private String httpReferer;
    private String httpUserAgent;
}
