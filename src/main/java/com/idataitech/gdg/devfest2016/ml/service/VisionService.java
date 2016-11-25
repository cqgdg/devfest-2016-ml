package com.idataitech.gdg.devfest2016.ml.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.idataitech.gdg.devfest2016.ml.support.HttpUtil;
import com.qiniu.api.FileManager;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;


/**
 * Created by wanggang on 16/11/10.
 */

@Service
public class VisionService {

    private String google_key=System.getProperty("GOOGLE_KEY", System.getenv("GOOGLE_KEY"));

    //Vision Api Url
    private String url = "https://content-vision.googleapis.com/v1/images:annotate?key="+google_key;

    //VisionFace Main
    public JSONObject visionResult(byte[] data) {
        long faceVisionStart = System.currentTimeMillis();
        JSONObject faceVision = vision(faceData(data));
        long faceVisionTime = System.currentTimeMillis() - faceVisionStart;

        JSONArray facePoint = getFacePoint(faceVision);

        long faceImageUrlStart = System.currentTimeMillis();
        String faceImageUrl = getFaceImageUrl(data, facePoint);
        long faceImageUrlTime = System.currentTimeMillis() - faceImageUrlStart;

        JSONObject result = getFaceSmileLevel(faceVision);
        result.put("faceImageUrl", faceImageUrl);
        result.put("visionTime", faceVisionTime);
        result.put("faceImageUrlTime", faceImageUrlTime);
        return result;
    }

    //分析图片
    public JSONObject vision(String body) {
        //结果集
        JSONObject results = new JSONObject();
        try {
            RequestBody requestBody = RequestBody.create(HttpUtil.JSON, body);
            Request request = new Request.Builder().url(url)
                    .post(requestBody).build();
            Response resp = HttpUtil.HTTP.newCall(request).execute();
            results.put("body", JSONObject.parse(resp.body().string()));
            return results;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //截取头像
    public String getFaceImageUrl(byte[] data, JSONArray facePoint) {


        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("jpg");
        ImageReader reader = readers.next();
        try {
            ImageInputStream imageStream = ImageIO.createImageInputStream(inputStream);
            reader.setInput(imageStream, true);
        } catch (Exception e) {
            //文件异常
        }
        ImageReadParam param = reader.getDefaultReadParam();

        int x = facePoint.getJSONObject(0).getIntValue("x");
        int y = facePoint.getJSONObject(0).getIntValue("y");
        int w = facePoint.getJSONObject(2).getIntValue("x") - x;
        int h = facePoint.getJSONObject(2).getIntValue("y") - y;

        Rectangle rect = new Rectangle(x, y, w, h);
        param.setSourceRegion(rect);

        String faceUrl = null;
        long myUpTime = 0;
        try {
            BufferedImage bi = reader.read(0, param);
            ImageIO.write(bi, "jpg", outputStream);

            faceUrl = upDateFace(outputStream.toByteArray());
            outputStream.close();
            inputStream.close();
        } catch (Exception e) {
            // "reader 异常";
        }
        return faceUrl;
    }


    //获取头像坐标
    public JSONArray getFacePoint(JSONObject faceVision) {
        return faceVision.getJSONObject("body")
                .getJSONArray("responses").getJSONObject(0)
                .getJSONArray("faceAnnotations").getJSONObject(0)
                .getJSONObject("boundingPoly").getJSONArray("vertices");
    }

    //判断心情
    public JSONObject getFaceSmileLevel(JSONObject faceVision) {

        JSONObject facejson = faceVision.getJSONObject("body")
                .getJSONArray("responses").getJSONObject(0)
                .getJSONArray("faceAnnotations").getJSONObject(0);


        JSONObject result = new JSONObject();

        //高兴的可能性
        String joyLikelihood = facejson.getString("joyLikelihood");
        //悲伤的可能性
        String sorrowLikelihood = facejson.getString("sorrowLikelihood");
        //愤怒可能性
        String angerLikelihood = facejson.getString("angerLikelihood");
        //惊喜可能性
        String surpriseLikelihood = facejson.getString("surpriseLikelihood");
        //裸露可能性
        String underExposedLikelihood = facejson.getString("underExposedLikelihood");
        //模糊可能性
        String blurredLikelihood = facejson.getString("blurredLikelihood");
        //头饰可能性
        String headwearLikelihood = facejson.getString("headwearLikelihood");

        //检测可信度
        double detectionConfidence = facejson.getDouble("detectionConfidence");

        double joy = expressionLevel(joyLikelihood);
        double sorrow = expressionLevel(sorrowLikelihood);
        double anger = expressionLevel(angerLikelihood);
        double surprise = expressionLevel(surpriseLikelihood);
        double headwear = expressionLevel(headwearLikelihood);

        double sl;
        if (joy > sorrow) {
            sl = joy * detectionConfidence;
        } else if (joy < sorrow) {
            sl = -sorrow * detectionConfidence;
        } else {
            sl = 0;
        }

        if (anger > 20) {
            result.put("anger", anger);
        }
        if (surprise > 20) {
            result.put("surprise", surprise);
        }
        if (headwear > 20) {
            result.put("headwear", headwear);
        }

        result.put("joyLikelihood", joyLikelihood);
        result.put("sorrowLikelihood", sorrowLikelihood);
        result.put("detectionConfidence", detectionConfidence);
        result.put("faceSmileLevel", sl);

        return result;
    }

    //表情等级
    public int expressionLevel(String str) {
        switch (str) {
            case "VERY_LIKELY":
                return 100;
            case "LIKELY":
                return 80;
            case "UNLIKELY":
                return 20;
            case "VERY_UNLIKELY":
                return 1;
        }
        return 0;
    }

    //构造请求参数
    public String faceData(byte[] data) {
        String facevalue = "{\n" +
                "  \"requests\": [\n" +
                "    {\n" +
                "      \"features\": [\n" +
                "        {\n" +
                "          \"type\": \"FACE_DETECTION\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"image\": {\n" +
                "        \"content\": \"" + Base64.encodeBase64URLSafeString(data) +
                " \"     }\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";

        return facevalue;
    }

    //保存截取的图片
    public String upDateFace(byte[] data) {
        FileManager fileManager = new FileManager();
        String key = "vision/" + Long.toString(System.currentTimeMillis()) + ".jpg";
        return fileManager.upData(data, key);
    }

    //VisionText Main
    public String visionTextResult(byte[] data) {
        JSONObject textVision = vision(TextData(data));
        return getText(textVision);
    }

    //文字识别请求
    public String TextData(byte[] data) {
        String textValue = "{\n" +
                "  \"requests\": [\n" +
                "    {\n" +
                "      \"features\": [\n" +
                "        {\n" +
                "          \"type\": \"TEXT_DETECTION\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"image\": {\n" +
                "        \"content\": \"" + Base64.encodeBase64URLSafeString(data) +
                " \"     }\n" +
                "    }\n" +
                "  ]\n" +
                "}\n";

        return textValue;
    }

    //获取文字
    public String getText(JSONObject textVision){
       return textVision.getJSONObject("body")
                .getJSONArray("responses").getJSONObject(0)
                .getJSONArray("textAnnotations").getJSONObject(0)
                .getString("description");
    }


}

