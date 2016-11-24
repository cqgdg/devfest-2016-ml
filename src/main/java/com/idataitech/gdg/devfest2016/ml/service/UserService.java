package com.idataitech.gdg.devfest2016.ml.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.chanjar.weixin.mp.bean.result.WxMpUser;

@Service
public class UserService {

    private Map<String, WxMpUser> users = new ConcurrentHashMap<>();

    public void addUser(WxMpUser user) {
        users.put(user.getOpenId(), user);
    }

    public WxMpUser getUser(String openid) {
        return users.get(openid);
    }

}
