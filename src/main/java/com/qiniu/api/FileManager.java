package com.qiniu.api;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangie on 2016/10/12.
 */
public class FileManager implements AuthFile {

    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    BucketManager bucketManager = new BucketManager(auth);

    //上传文件
    public String upData(byte[] data, String key) {
        UploadManager uploadManager = new UploadManager();
        String upToken = auth.uploadToken(bucket);
        String domain = "http://dl.wangie.cn/";
        try {
            uploadManager.put(data, key, upToken);
        } catch (Exception e) {
            //上传失败
            return "上传异常";
        }

        return domain + key;
    }

    //抓取文件
    public String getFetchUrl(String mediaId, String token,String format) {
        String url = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=" + token + "&media_id=" + mediaId;
        String key = "/audio/" + System.currentTimeMillis() + format;
        String result = "";
        try {
            bucketManager.fetch(url, bucket, key);
            result = "http://dl.wangie.cn/" + key;
        } catch (Exception e) {

        }
        return result;
    }
}
