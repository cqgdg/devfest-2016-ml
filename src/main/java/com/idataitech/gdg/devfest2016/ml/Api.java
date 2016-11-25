package com.idataitech.gdg.devfest2016.ml;

import com.alibaba.fastjson.JSONObject;
import com.idataitech.gdg.devfest2016.ml.service.StorgeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Api {

    @Autowired
    private StorgeService storgeService;

    @RequestMapping("/api/faces")
    public List<JSONObject> faces() {
        return storgeService.getFaces();
    }

    @RequestMapping("/api/ocrs")
    public List<JSONObject> ocrs() {
        return storgeService.getOcrs();
    }

    @RequestMapping("/api/voices")
    public List<JSONObject> voices() {
        return storgeService.getVoices();
    }


}
