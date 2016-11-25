package com.idataitech.gdg.devfest2016.ml;

import com.alibaba.fastjson.JSONObject;
import com.idataitech.gdg.devfest2016.ml.service.UserService;

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

@RestController
public class WxApi {

    @Autowired
    private UserService userService;
    @Autowired
    private WxMpService wxMpService;

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
        JSONObject result = new JSONObject();
        result.put("img", "");
        return result;
    }

    @RequestMapping("/api/wx/ocr")
    public JSONObject ocr(@RequestParam String mediaId) {
        JSONObject result = new JSONObject();
        result.put("text", "");
        return result;
    }

    @RequestMapping("/api/wx/voice")
    public JSONObject voice(@RequestParam String mediaId) {
        JSONObject result = new JSONObject();
        result.put("text", "");
        return result;
    }


}
