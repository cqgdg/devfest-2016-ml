package com.qiniu.api;

/**
 * Created by wangie on 2016/9/15.
 */
public interface AuthFile {
    String bucket = System.getProperty("QN_BACKET", System.getenv("QN_BACKET"));
    String ACCESS_KEY = System.getProperty("QN_ACCESS_KEY", System.getenv("QN_ACCESS_KEY"));
    String SECRET_KEY = System.getProperty("QN_SECRET_KEY", System.getenv("QN_SECRET_KEY"));
}
