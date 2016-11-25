package com.idataitech.gdg.devfest2016.ml;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.idataitech.gdg.devfest2016.ml.service.FileService;
import com.idataitech.gdg.devfest2016.ml.service.GoogleService;
import com.idataitech.gdg.devfest2016.ml.service.StorgeService;
import com.idataitech.gdg.devfest2016.ml.service.UserService;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

@RestController
public class WxApi {

    @Autowired
    private UserService userService;
    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private FileService fileService;
    @Autowired
    private GoogleService googleService;
    @Autowired
    private StorgeService storgeService;

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
    public JSONObject face(@RequestParam String mediaId, HttpSession session) throws Exception {
        String openid = (String) session.getAttribute("openid");
        WxMpUser user = userService.getUser(openid);

        String imgUrl = fileService.downloadFromWx(wxMpService.getAccessToken(), mediaId, ".jpg");
        byte[] data = IOUtils.toByteArray(new URL(imgUrl).openStream());
        JSONObject result = googleService.faceDetect(data);

        // 截取头像
        JSONArray facePoints = result.getJSONArray("facePoints");
        ByteArrayOutputStream faceImageOut = new ByteArrayOutputStream();
        ImageReader reader = ImageIO.getImageReadersByFormatName("jpg").next();
        reader.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(data)), true);
        ImageReadParam param = reader.getDefaultReadParam();
        int x = facePoints.getJSONObject(0).getIntValue("x");
        int y = facePoints.getJSONObject(0).getIntValue("y");
        int w = facePoints.getJSONObject(2).getIntValue("x") - x;
        int h = facePoints.getJSONObject(2).getIntValue("y") - y;
        param.setSourceRegion(new Rectangle(x, y, w, h));
        ImageIO.write(reader.read(0, param), "jpg", faceImageOut);
        String faceUrl = fileService.localUpload(faceImageOut.toByteArray(), ".jpg");

        result.put("imgUrl", imgUrl);
        result.put("faceUrl", faceUrl);
        result.put("user", user);
        result.put("created", new Date());
        storgeService.saveFace(result);
        return result;
    }

    @RequestMapping("/api/wx/ocr")
    public JSONObject ocr(@RequestParam String mediaId, HttpSession session) throws Exception {
        String openid = (String) session.getAttribute("openid");
        WxMpUser user = userService.getUser(openid);

        String imgUrl = fileService.downloadFromWx(wxMpService.getAccessToken(), mediaId, ".jpg");
        byte[] data = IOUtils.toByteArray(new URL(imgUrl).openStream());
        JSONObject result = new JSONObject();
        result.put("imgUrl", imgUrl);
        result.put("text", googleService.ocr(data));
        result.put("user", user);
        result.put("created", new Date());
        storgeService.saveOcr(result);
        return result;
    }

    @RequestMapping("/api/wx/voice")
    public JSONObject voice(@RequestParam String mediaId, HttpSession session) throws Exception {
        String openid = (String) session.getAttribute("openid");
        WxMpUser user = userService.getUser(openid);

        String amrUrl = fileService.downloadFromWx(wxMpService.getAccessToken(), mediaId, ".amr");
        String mp3Url = fileService.amr2mp3(amrUrl.replace(fileService.getHost(), ""));
        byte[] data = IOUtils.toByteArray(new URL(amrUrl).openStream());
        JSONObject result = new JSONObject();
        result.put("amrUrl", amrUrl);
        result.put("mp3Url", mp3Url);
        result.put("text", googleService.voiceToText(data));
        result.put("user", user);
        result.put("created", new Date());
        storgeService.saveOcr(result);
        return result;
    }

}

