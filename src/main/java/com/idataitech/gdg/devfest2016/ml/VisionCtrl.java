package com.idataitech.gdg.devfest2016.ml;

import com.alibaba.fastjson.JSONObject;
import com.idataitech.gdg.devfest2016.ml.bean.JsonResult;
import com.idataitech.gdg.devfest2016.ml.service.VisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by wanggang on 16/11/15.
 */

@RestController("visionCtrl")
public class VisionCtrl {

    @Autowired
    @Qualifier("visionService")
    private VisionService visionService;

//    //列表
//    @RequestMapping(method = RequestMethod.GET, value = "/api/admin/vision")
//    public JsonResult<List<JSONObject>> VisionList(@RequestParam String size) {
//        return JsonResult.success(visionService.visionList(null));
//    }




}
