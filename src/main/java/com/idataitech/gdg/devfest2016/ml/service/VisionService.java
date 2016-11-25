package com.idataitech.gdg.devfest2016.ml.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.idataitech.gdg.devfest2016.ml.support.HttpUtil;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by wanggang on 16/11/10.
 */

@Service
public class VisionService {

    private String key = System.getProperty("GOOGLE_KEY", System.getenv("GOOGLE_KEY"));

    //Vision Api Url
    private String url = "https://content-vision.googleapis.com/v1/images:annotate?key=" + key;

    //VisionFace Main
    public JSONObject faceDetect(byte[] data) throws Exception {
        long faceVisionStart = System.currentTimeMillis();

        //构造请求参数
        JSONObject image = new JSONObject();
        image.put("content", data);

        JSONArray features = new JSONArray();
        JSONObject feature = new JSONObject();
        feature.put("type", "FACE_DETECTION");
        features.add(feature);

        JSONArray requests = new JSONArray();
        JSONObject request = new JSONObject();
        request.put("image", image);
        request.put("features", features);
        requests.add(request);

        JSONObject message = new JSONObject();
        message.put("requests", requests);

        RequestBody requestBody = RequestBody.create(HttpUtil.JSON, message.toJSONString());
        Request req = new Request.Builder().url(url).post(requestBody).build();
        Response resp = HttpUtil.HTTP.newCall(req).execute();
        long faceVisionTime = System.currentTimeMillis() - faceVisionStart;
        JSONObject apiResult = (JSONObject) JSONObject.parse(resp.body().bytes());

        // 获取头像坐标
        JSONObject faceAnnotation = apiResult.getJSONArray("responses").getJSONObject(0).getJSONArray("faceAnnotations").getJSONObject(0);
        calFaceSmileLevel(faceAnnotation);

        JSONArray facePoints = faceAnnotation.getJSONObject("boundingPoly").getJSONArray("vertices");
        faceAnnotation.put("facePoints", facePoints);
        faceAnnotation.put("visionTime", faceVisionTime);
        return faceAnnotation;
    }


    //判断心情
    public void calFaceSmileLevel(JSONObject faceVision) {
        String joyLikelihood = faceVision.getString("joyLikelihood");                  //高兴的可能性
        String sorrowLikelihood = faceVision.getString("sorrowLikelihood");            //悲伤的可能性
        String angerLikelihood = faceVision.getString("angerLikelihood");              //愤怒可能性
        String surpriseLikelihood = faceVision.getString("surpriseLikelihood");        //惊喜可能性
        String underExposedLikelihood = faceVision.getString("underExposedLikelihood");//裸露可能性
        String blurredLikelihood = faceVision.getString("blurredLikelihood");          //模糊可能性
        String headwearLikelihood = faceVision.getString("headwearLikelihood");        //头饰可能性
        double detectionConfidence = faceVision.getDouble("detectionConfidence");      //检测可信度


        double joy = expressionLevel(joyLikelihood);
        double sorrow = expressionLevel(sorrowLikelihood);
        double anger = expressionLevel(angerLikelihood);
        double surprise = expressionLevel(surpriseLikelihood);
        double underExposed = expressionLevel(underExposedLikelihood);
        double blurred = expressionLevel(blurredLikelihood);
        double headwear = expressionLevel(headwearLikelihood);

        double faceSmileLevel = 0;
        if (joy > sorrow) {
            faceSmileLevel = joy * detectionConfidence;
        } else if (joy < sorrow) {
            faceSmileLevel = -sorrow * detectionConfidence;
        }

        faceVision.put("joy", joy);
        faceVision.put("sorrow", sorrow);
        faceVision.put("anger", anger);
        faceVision.put("surprise", surprise);
        faceVision.put("underExposed", underExposed);
        faceVision.put("blurred", blurred);
        faceVision.put("headwear", headwear);
        faceVision.put("detectionConfidence", detectionConfidence);
        faceVision.put("faceSmileLevel", faceSmileLevel);
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
                return 0;
        }
        return 0;
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
    public String getText(JSONObject textVision) {
        return textVision.getJSONObject("body")
                .getJSONArray("responses").getJSONObject(0)
                .getJSONArray("textAnnotations").getJSONObject(0)
                .getString("description");
    }


}

