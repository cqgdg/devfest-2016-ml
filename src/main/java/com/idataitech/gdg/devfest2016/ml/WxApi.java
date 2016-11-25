package com.idataitech.gdg.devfest2016.ml;

import com.alibaba.fastjson.JSONObject;
import com.idataitech.gdg.devfest2016.ml.service.UserService;

import com.idataitech.gdg.devfest2016.ml.service.VisionService;
import com.idataitech.gdg.devfest2016.ml.support.HttpUtil;
import com.qiniu.api.FileManager;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
public class WxApi {

    @Autowired
    private UserService userService;
    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private VisionService visionService;

    @RequestMapping("/api/wx/session")
    public WxMpUser session(HttpSession session) {
        String openid = (String) session.getAttribute("openid");
        if (openid != null) {
            return userService.getUser(openid);
        }
        return null;
    }

    @RequestMapping("/api/wx/jsapi")
    public WxJsapiSignature jsapi(HttpServletRequest request) throws WxErrorException {
        return wxMpService.createJsapiSignature(request.getHeader("Referer"));
    }

    @RequestMapping("/api/wx/face")
    public JSONObject face(@RequestParam String mediaId) {

        return ocr(mediaId);
//        JSONObject result = null;
//
//        FileManager fileManager = new FileManager();
//        String fileUrl = fileManager.getFetchUrl(mediaId, getToken(), ".jpg");
//        byte[] data = downFile(fileUrl);
//        result = visionService.visionResult(data);
//        return result;
    }

    @RequestMapping("/api/wx/ocr")
    public JSONObject ocr(@RequestParam String mediaId) {
        JSONObject result = new JSONObject();
        FileManager fileManager = new FileManager();
        String fileUrl = fileManager.getFetchUrl(mediaId, getToken(), ".jpg");
        byte[] data = downFile(fileUrl);
        result.put("text", visionService.visionTextResult(data));
        return result;
    }

    @RequestMapping("/api/wx/voice")
    public JSONObject voice(@RequestParam String mediaId) {
        JSONObject result = new JSONObject();
        result.put("text", "");
        return result;
    }

    public String getToken() {

        String appId = System.getProperty("WX_APP_ID", System.getenv("WX_APP_ID"));
        String appSecret = System.getProperty("WX_APP_SECRET", System.getenv("WX_APP_SECRET"));

        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
        JSONObject result = new JSONObject();
        try {
            Request request = new Request.Builder().url(url).get().build();
            Response resp = HttpUtil.HTTP.newCall(request).execute();
            result.put("body", JSONObject.parse(resp.body().string()));
        } catch (Exception e) {
            // return "null";
        }
        String token = result.getJSONObject("body").getString("access_token");
        return token;

    }

    public byte[] downFile(String urlStr) {
        byte[] data;
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            //得到输入流
            InputStream inputStream = conn.getInputStream();
            //获取自己数组
            data = input2byte(conn.getInputStream());
        } catch (Exception e) {
            data = null;
        }

        return data;
    }

    public byte[] input2byte(InputStream inStream) {
        byte[] in2b;
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = inStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            in2b = swapStream.toByteArray();
        } catch (Exception e) {
            in2b = null;
        }

        return in2b;
    }

    @RequestMapping("/api/amr2mp3")
    public String amr2mp3() {
        FileManager fileManager = new FileManager();
        return fileManager.amr2mp3("audio/1480039480737.amr");
    }

}