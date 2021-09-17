package com.demo.elasticsearch.Model;

/**
 * 响应码枚举，参考HTTP状态码的语义
 * @author jky
 */
public enum ResultCode {
    SUCCESS(200),//成功
    FAIL(400),//失败
    UNAUTHORIZED(401),//未认证（签名错误）
    NOT_FOUND(404),//接口不存在
    ERROR_NAMEORPASS(10004),//获取 token 时用户名或密码错误
    TOKEN_TIMEOUT(10006),//获取 userToken 时 token 失效
    ERROR_PARAM(10007),//获取 userToken 时 入参格式错误
    ERROR_SIGN(10008),//获取 userToken 时 验签失败
    NO_REGISTER(10010),//获取 userToken 时 数据未找到（可认为是用户未注册一码通）
    INTERNAL_SERVER_ERROR(500);//服务器内部错误

    public int code;

    ResultCode(int code) {
        this.code = code;
    }
}
