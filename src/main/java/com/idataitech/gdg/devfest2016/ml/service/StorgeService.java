package com.idataitech.gdg.devfest2016.ml.service;

import com.alibaba.fastjson.JSONObject;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by idit on 25/11/2016.
 */
@Service
public class StorgeService {

    private List<JSONObject> faceStorge = Collections.synchronizedList(new ArrayList<JSONObject>());

    public void saveFace(JSONObject data) {
        faceStorge.add(data);
    }

    public List<JSONObject> getFaces() {
        return Collections.unmodifiableList(faceStorge);
    }

}
