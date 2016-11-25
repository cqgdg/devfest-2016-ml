package com.idataitech.gdg.devfest2016.ml.service;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;

import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by wangie on 2016/10/12.
 */
@Service
public class FileService {

    private String host = System.getProperty("QN_HOST", System.getenv("QN_HOST"));
    private String bucket = System.getProperty("QN_BACKET", System.getenv("QN_BACKET"));
    private String accessKey = System.getProperty("QN_ACCESS_KEY", System.getenv("QN_ACCESS_KEY"));
    private String secretKey = System.getProperty("QN_SECRET_KEY", System.getenv("QN_SECRET_KEY"));

    private Auth auth = Auth.create(accessKey, secretKey);
    private BucketManager bucketManager = new BucketManager(auth);
    private UploadManager uploadManager = new UploadManager();

    // 从微信服务器下载文件
    public String downloadFromWx(String token, String mediaId, String format) throws QiniuException {
        String key = "devfest-2016-ml/" + UUID.randomUUID() + format;
        bucketManager.fetch(
                "https://api.weixin.qq.com/cgi-bin/media/get?access_token=" + token + "&media_id=" + mediaId,
                bucket, key
        );
        return host + key;
    }

    public String localUpload(byte[] data, String format) throws QiniuException {
        String key = "devfest-2016-ml/" + UUID.randomUUID() + format;
        uploadManager.put(data, key, auth.uploadToken(bucket));
        return host + key;
    }

}
