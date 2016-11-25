package com.idataitech.gdg.devfest2016.ml.service;

import com.alibaba.fastjson.JSONObject;
import com.idataitech.gdg.devfest2016.ml.support.HttpUtil;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;

/**
 * Created by wanggang on 16/11/24.
 */
//语音识别
public class SpeechService {

    private String google_key=System.getProperty("GOOGLE_KEY", System.getenv("GOOGLE_KEY"));

    //Speech Api Url
    private String url = "https://content-vision.googleapis.com/v1/images:annotate?key="+google_key;


    //SpeechService Main
    public String speechResult(byte[] data) {
        String body = speechData(data);

        JSONObject result = speech(body);

        String text = getText(result);

        return text;
    }

    //分析语音
    public JSONObject speech(String body) {
        //结果集
        JSONObject results = new JSONObject();
        try {
            RequestBody requestBody = RequestBody.create(HttpUtil.JSON, body);
            Request request = new Request.Builder().url(url)
                    .post(requestBody).build();
            Response resp = HttpUtil.HTTP.newCall(request).execute();
            results.put("body", JSONObject.parse(resp.body().string()));
            return results;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //构造请求参数
    public String speechData(byte[] data) {

        String speechvalue = "{\n" +
                " \"audio\": {\n" +
                " \"content\":\"" + Base64.encodeBase64URLSafeString(data) +
                "\" },\n" +
                " \"config\": {\n" +
                "  \"encoding\": \"AMR\",\n" +
                "\"sampleRate\" : 8000," +
                "  \"languageCode\": \"cmn-Hans-CN\"\n" +
                " }\n" +
                "}";

        return speechvalue;
    }


    public String getText(JSONObject jsonObject) {
        String result = jsonObject.getJSONObject("body")
                .getJSONArray("results").getJSONObject(0)
                .getJSONArray("alternatives").getJSONObject(0)
                .getString("transcript");
        return result;
    }

    public void test() {


    }
}
