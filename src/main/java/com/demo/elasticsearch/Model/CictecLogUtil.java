package com.demo.elasticsearch.Model;

import com.alibaba.fastjson.JSON;

import java.util.Date;

/**
 * @author :jky
 * @Description: 日志工具类
 * @ Date: Created in 2021-05-08 10:16
 */
public class CictecLogUtil {

    /**
     * 日志输出格式化
     * @param content 日志内容，格式：字符串
     * @return 格式化后的日志信息
     */
    public static String  logResult(String content){
        return logResult("main",content,"1","");
    }

    /**
     * 日志输出格式化
     * @param serverName 日志输出的那个微服务名称
     * @param content 日志内容，格式：字符串
     * @return 格式化后的日志信息
     */
    public static String  logResult(String serverName,String content){
        return logResult(serverName,content,"1","");
    }

    /**
     * 日志输出格式化
     * @param serverName 日志输出的那个微服务名称
     * @param content 日志内容，格式：字符串
     * @param userName 日志操作者
     * @return 格式化后的日志信息
     */
    public static String  logResult(String serverName,String content,String userName){
        return logResult(serverName,content,userName,"");
    }
    /**
     * 日志输出格式化
     * @param serverName 日志输出的那个微服务名称
     * @param content 日志内容，格式：字符串
     * @param userName 日志操作者
     * @param additionalInfo 日志附加信息，json格式
     * @return 格式化后的日志信息
     */
    public static String  logResult(String serverName,String content,String userName,String additionalInfo){
        CictecLog cictecLog = new CictecLog();
        cictecLog.setServerLog("demo");
        cictecLog.setServerName(serverName);
        cictecLog.setDescribe(content);
        cictecLog.setUserName(userName);
        cictecLog.setCreateDate(new Date());
        cictecLog.setAdditionalInfo(additionalInfo);
        return JSON.toJSONString(cictecLog);
    }

}
