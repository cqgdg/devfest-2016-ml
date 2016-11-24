package com.idataitech.gdg.devfest2016.ml;

import com.idataitech.gdg.devfest2016.ml.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

import me.chanjar.weixin.mp.bean.result.WxMpUser;

@RestController
public class WxApi {

    @Autowired
    private UserService userService;

    @RequestMapping("/api/wx/session")
    public WxMpUser session(HttpSession session) {
        String openid = (String) session.getAttribute("openid");
        if (openid != null) {
            return userService.getUser(openid);
        }
        return null;
    }


}
