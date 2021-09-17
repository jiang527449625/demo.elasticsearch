package com.demo.elasticsearch.Model;

import com.demo.elasticsearch.Model.Head;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应结果生成工具
 */
public class ResultGenerator {
    private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";

    /**
     *
     * @return
     */
    public static Result genSuccessResult() {
        return genSuccessResult(DEFAULT_SUCCESS_MESSAGE, new HashMap());
    }

    /**
     *
     * @param data 返回成功的数据对象
     * @return
     */
    public static Result genSuccessResult(Object data) {
        return genSuccessResult(DEFAULT_SUCCESS_MESSAGE, data);
    }

    /**
     *
     * @param message 成功的msg信息
     * @param data 返回的数据对象
     * @return
     */
    public static Result genSuccessResult(String message, Object data) {
        Head head = new Head(message, ResultCode.SUCCESS, "true");

        return new Result(head, data);
    }

    /**
     *
     * @param message 需要提示的信息
     * @param flag error：表示错误信息
     *             warn：表示警告信息
     *             success：表示正常信息
     * @return
     */
    public static Result genFailResult(String message, String flag) {
        Head head = new Head(message, ResultCode.FAIL, flag);
        return new Result(head, message);
    }

    /**
     *
     * @param message 需要提示的信息
     * @param code error：表示错误信息
     *             warn：表示警告信息
     *             success：表示正常信息
     * @return
     */
    public static Result genFailResult(String message, Integer code) {
        Head head = new Head(message, code, "false");
        return new Result(head, message);
    }

    /**
     * 默认错误的提示
     * @param message 需要提示的信息
     * @return
     */
    public static Result genFailResult(String message) {
        return genFailResult(message,"error");
    }

    public static Result genSuccessResult(ResultCode code, String message, Object rdata) {
        Map<String,Object> data = new HashMap<String,Object>();
        data.put("success",code== ResultCode.SUCCESS?true:false);
        data.put("data",rdata);
        data.put("message",message);
        return genSuccessResult(DEFAULT_SUCCESS_MESSAGE, data);
    }

    /**
     * 默认错误的提示，提示信息为'失败'
     * @return
     */
    public static Result genFailResult() {
        return genFailResult("失败");
    }

    /**
     * 服务器内部错误
     * @return
     */
    public static Result genServiceErrorResult(Object object) {
        Head head = new Head("服务器内部错误", ResultCode.INTERNAL_SERVER_ERROR, "error");
        return new Result(head, object);
    }

    /**
     * 接口不存在
     * @return
     */
    public static Result genNoIntefaceResult() {
        Head head = new Head("接口不存在", ResultCode.NOT_FOUND, "error");
        return new Result(head, "接口不存在");
    }

    /**
     * 认证错误
     * @param msg 提示的信息
     * @return
     */
    public static Result genUnauthorizedResult(String msg) {
        Head head = new Head(msg, ResultCode.UNAUTHORIZED, "error");
        return new Result(head, msg);
    }

}
