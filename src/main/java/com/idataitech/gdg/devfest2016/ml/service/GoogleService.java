package com.idataitech.gdg.devfest2016.ml.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.idataitech.gdg.devfest2016.ml.support.HttpUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by wanggang on 16/11/10.
 */

@Service
public class GoogleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleService.class);
    private String key = System.getProperty("GOOGLE_KEY", System.getenv("GOOGLE_KEY"));
    private String visionURL = "https://content-vision.googleapis.com/v1/images:annotate?key=" + key;
    private String speechURL = "https://speech.googleapis.com/v1beta1/speech:syncrecognize?key=" + key;

    // 面部识别
    public JSONObject faceDetect(byte[] data) throws Exception {
        LOGGER.debug("key: {}", key);
        long faceVisionStart = System.currentTimeMillis();

        JSONObject message = getRequestMessage("FACE_DETECTION", data);
        JSONObject apiResult = executeRequest(visionURL, message);
        long faceVisionTime = System.currentTimeMillis() - faceVisionStart;

        // 获取头像坐标
        JSONObject faceAnnotation = apiResult.getJSONArray("responses").getJSONObject(0).getJSONArray("faceAnnotations").getJSONObject(0);
        calFaceSmileLevel(faceAnnotation);

        JSONArray facePoints = faceAnnotation.getJSONObject("boundingPoly").getJSONArray("vertices");
        faceAnnotation.put("facePoints", facePoints);
        faceAnnotation.put("visionTime", faceVisionTime);
        return faceAnnotation;
    }

    public String ocr(byte[] data) throws Exception {
        JSONObject message = getRequestMessage("TEXT_DETECTION", data);
        JSONObject apiResult = executeRequest(visionURL, message);
        return apiResult.getJSONArray("responses").getJSONObject(0)
                .getJSONArray("textAnnotations").getJSONObject(0)
                .getString("description");
    }

    public String voiceToText(byte[] data) throws Exception {
        JSONObject message = new JSONObject();

        JSONObject audio = new JSONObject();
        audio.put("content", data);
        message.put("audio", audio);

        JSONObject config = new JSONObject();
        config.put("encoding", "AMR");
        config.put("sampleRate", 8000);
        config.put("languageCode", "cmn-Hans-CN");
        message.put("config", config);

        JSONObject apiResult = executeRequest(speechURL, message);
        return apiResult.getJSONArray("results").getJSONObject(0)
                .getJSONArray("alternatives").getJSONObject(0)
                .getString("transcript");
    }

    private JSONObject getRequestMessage(String type, byte[] data) {
        JSONObject image = new JSONObject();
        image.put("content", data);

        JSONArray features = new JSONArray();
        JSONObject feature = new JSONObject();
        feature.put("type", type);
        features.add(feature);

        JSONArray requests = new JSONArray();
        JSONObject request = new JSONObject();
        request.put("image", image);
        request.put("features", features);
        requests.add(request);

        JSONObject message = new JSONObject();
        message.put("requests", requests);
        return message;
    }

    private JSONObject executeRequest(String url, JSONObject message) throws Exception {
        RequestBody requestBody = RequestBody.create(HttpUtil.JSON, message.toJSONString());
        Request req = new Request.Builder().url(url).post(requestBody).build();
        Response resp = HttpUtil.HTTP.newCall(req).execute();
        JSONObject apiResult = (JSONObject) JSONObject.parse(resp.body().bytes());
        LOGGER.info("Google api result: ", apiResult);
        return apiResult;
    }

    //判断心情
    private void calFaceSmileLevel(JSONObject faceVision) {
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
    private int expressionLevel(String str) {
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

}

