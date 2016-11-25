package com.idataitech.gdg.devfest2016.ml.support;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * HTTP 工具类
 */
public final class HttpUtil {

    public static final OkHttpClient HTTP;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    public static String getIP() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();
        return request.getRemoteAddr();
    }


    static {
        HTTP = new OkHttpClient();
//        HTTP.setConnectTimeout(60, TimeUnit.SECONDS);
//        HTTP.setReadTimeout(60, TimeUnit.SECONDS);
//        HTTP.setWriteTimeout(60, TimeUnit.SECONDS);
    }

}
