package com.idataitech.gdg.devfest2016.ml.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpServiceImpl;

@Configuration("wxConfig")
public class WxConfig {

    private String appId = System.getProperty("WX_APP_ID", System.getenv("WX_APP_ID"));
    private String appSecret = System.getProperty("WX_APP_SECRET", System.getenv("WX_APP_SECRET"));

    private WxMpInMemoryConfigStorage config;
    private WxMpService wxMpService;

    @Bean(name = {"wxMpConfigStorage"})
    public WxMpInMemoryConfigStorage wxMpConfigStorage() {
        config = new WxMpInMemoryConfigStorage();
        config.setAppId(appId);
        config.setSecret(appSecret);
        return config;
    }

    @Bean(name = {"wxMpService"})
    public WxMpService wxMpService(WxMpInMemoryConfigStorage config) {
        wxMpService = new WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(config);
        return wxMpService;
    }


}
