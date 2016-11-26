package com.idataitech.gdg.devfest2016.ml.service;

import com.qiniu.common.QiniuException;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;

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
    private OperationManager operater = new OperationManager(auth);

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

    public String amr2mp3(String key) throws QiniuException {
        String newKey = "devfest-2016-ml/" + UUID.randomUUID() + ".mp3";
        //设置转码操作参数
        String fops = "avthumb/mp3/ab/192k/ar/44100/acodec/libmp3lame";
        //设置转码的队列
        String pfops = fops + "|saveas/" + UrlSafeBase64.encodeToString(bucket + ':' + newKey);
        //设置pipeline参数
        StringMap params = new StringMap().putWhen("force", 1, true).putNotEmpty("pipeline", "amr2mp3");
        operater.pfop(bucket, key, pfops, params);
        return host + key;
    }

    public String getHost() {
        return host;
    }

}
