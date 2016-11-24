package com.idataitech.gdg.devfest2016.ml;

import com.idataitech.gdg.devfest2016.ml.service.UserService;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

@Controller
public class Wx {

    @Autowired
    private UserService userService;
    @Autowired
    private WxMpService wxMpService;

    @RequestMapping("/wx/login")
    public String login(@RequestParam(required = false) String code,
                        @RequestParam(required = false) String state,
                        @RequestParam(defaultValue = "/wx/vision.html") String redirect,
                        HttpServletRequest request, HttpSession session) throws Exception {
        String openid = (String) session.getAttribute("openid");
        if (openid == null) {
            if (StringUtils.isBlank(code) && StringUtils.isBlank(state)) {
                redirect = request.getScheme() + "://"
                        + (request.getHeader("Domain-Proxy") != null ? "domainproxy.idataitech.com" : request.getHeader("Host"))
                        + request.getRequestURI();
                String query = request.getQueryString();
                if (query != null) {
                    redirect = redirect + "?" + query;
                }
                return "redirect:" + wxMpService.oauth2buildAuthorizationUrl(redirect, "snsapi_userinfo", RandomStringUtils.randomAlphanumeric(8));
            }

            WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
            WxMpUser user = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken, null);
            userService.addUser(user);
            openid = user.getOpenId();
            session.setAttribute("openid", openid);
        }
        return "redirect:" + redirect;
    }

}
