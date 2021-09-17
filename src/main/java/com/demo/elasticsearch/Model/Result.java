package com.demo.elasticsearch.Model;

import com.alibaba.fastjson.JSON;
import com.demo.elasticsearch.Model.Head;

/**
 * 统一API响应结果封装
 */
public class Result {
    private int code;
    private String message;
    private Object data;
    private Head head;
    public Result(){

    }
    public Result( Head head,Object data){
        this.head=head;
        this.data=data;
    }
    public Result setCode(ResultCode resultCode) {
        this.code = resultCode.code;
        return this;
    }

    public int getCode() {
        return code;
    }

    public Result setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Result setData(Object data) {
        this.data = data;
        return this;
    }

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
