package com.qiniu.api;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;

import javax.security.auth.login.Configuration;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangie on 2016/10/12.
 */
public class FileManager implements AuthFile {

    private Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    private BucketManager bucketManager = new BucketManager(auth);
    private String domain = "http://dl.wangie.cn/";

    //上传文件
    public String upData(byte[] data, String key) {
        UploadManager uploadManager = new UploadManager();
        String upToken = auth.uploadToken(bucket);
        try {
            uploadManager.put(data, key, upToken);
        } catch (Exception e) {
            //上传失败
            return "上传异常";
        }

        return domain + key;
    }

    //抓取文件
    public String getFetchUrl(String mediaId, String token, String format) {
        String url = "https://api.weixin.qq.com/cgi-bin/media/get?access_token=" + token + "&media_id=" + mediaId;
        String key = "/gdg/" + System.currentTimeMillis() + format;
        String result = "";
        try {
            bucketManager.fetch(url, bucket, key);
            result = domain + key;
        } catch (Exception e) {

        }
        return result;
    }

    //转码
    public String amr2mp3(String key) {

        //新建一个OperationManager对象
        OperationManager operater = new OperationManager(auth);
        //设置要转码的空间和key，并且这个key在你空间中存在
        //设置转码操作参数
        String fops = "avthumb/mp3/ab/192k/ar/44100/acodec/libmp3lame";
        //设置转码的队列
        String pipeline = "amr2mp3";
        //可以对转码后的文件进行使用saveas参数自定义命名，当然也可以不指定文件会默认命名并保存在当前空间。
        String urlbase64 = UrlSafeBase64.encodeToString(bucket+":mp3/"+key);

        String pfops = fops + "|saveas/"+urlbase64;
        //设置pipeline参数
        StringMap params = new StringMap().putWhen("force", 1, true).putNotEmpty("pipeline", pipeline);
        try {
            String persistid = operater.pfop(bucket, key, pfops, params);

            return domain +"mp3/"+  key;
        } catch (QiniuException e) {
            //捕获异常信息
            Response r = e.response;
            // 请求失败时简单状态信息
            System.out.println(r.toString());
            try {
                // 响应的文本信息
                System.out.println(r.bodyString());
            } catch (QiniuException e1) {
                //ignore
            }
        }
        return null;
    }
}
