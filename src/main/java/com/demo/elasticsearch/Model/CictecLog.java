package com.demo.elasticsearch.Model;

import lombok.Data;

import java.util.Date;

/**
 * @author :jky
 * @Description:
 * @ Date: Created in 2021-05-08 13:54
 */
@Data
public class CictecLog {
    private String serverLog;
    private String ip;
    private Integer port;
    private String serverName;
    private String userName;
    private String className;
    private String threadName;
    private String methodName;
    private String describe;
    private Date createDate;
    private String additionalInfo;
}
