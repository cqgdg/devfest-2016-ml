package com.idataitech.gdg.devfest2016.ml.bean;

/**
 * 错误代码
 */
public enum ErrorCode {

    AUTHORIZATION_REQUIRED(401, "需要重新登录"),// 借鉴 HTTP 401

    LOGIN_FAILD(4011, "登陆失败"),// 借鉴 HTTP 401.1
    ILLEGAL_ASSCESS(20000, "非法操作"),

    INTEGRITY_CONSTRAINT(20001, "数据被使用中"),
    DATA_TRUNCATION(20002, "数据太长:"),

    PHONE_INVALID(20003, "手机号不正确"),
    PHONE_DUPLICATE(20004, "手机号重复"),
    USERNAME_INVALID(20005, "用户名不符合要求"),
    USERNAME_DUPLICATE(20006, "用户名重复"),
    UNKNOWN(0, "未知(非预期)错误");

    int code;
    String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
