package com.fct.common.service.qcloud;
import java.util.TreeMap;

import com.fct.common.service.Constants;
import com.fct.common.service.qcloud.Module.Vod;
import com.fct.common.service.qcloud.Utilities.Json.JSONObject;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.meta.InsertOnly;
import com.qcloud.cos.request.UploadFileRequest;
import com.qcloud.cos.sign.Credentials;

public class VodApi {

        public static JSONObject upload(String secretId, String secretKey, byte[] vidoeFile, String fileType) {
            return upload(secretId, secretKey,vidoeFile,fileType, null);
        }

        public static JSONObject upload(String secretId, String secretKey,byte[] videoFile, String fileType, String coverPath) {
                TreeMap<String, Object> config = new TreeMap<String, Object>();
                config.put("SecretId", secretId);
                config.put("SecretKey", secretKey);
                config.put("RequestMethod", "POST");
                QcloudApiModuleCenter module = new QcloudApiModuleCenter(new Vod(),config);

                // 第一步，发起上传
                TreeMap<String, Object> params = new TreeMap<String, Object>();
                params.put("videoType", fileType);
                if(coverPath != null) {
                        String[] coverPathSplit = coverPath.split("\\.");
                        params.put("coverType", coverPathSplit[coverPathSplit.length - 1]);
                }
                String result = null;
                try {
                        result = module.call("ApplyUpload", params);
                } catch (Exception e) {
                        Constants.logger.error("upload call error:"+e.getMessage());
                }

                JSONObject json_result = new JSONObject(result);
                Constants.logger.info("ApplyUpload|recv:" + json_result);

                String bucket = json_result.getString("storageBucket");
                String region = json_result.getString("storageRegion");
                String vodSessionKey = json_result.getString("vodSessionKey");
                String videoDst = json_result.getJSONObject("video").getString("storagePath");
                String coverDst = null;
                if(coverPath != null) {
                        coverDst = json_result.getJSONObject("cover").getString("storagePath");
                }

                // 第二步，上传文件到COS
                long appId = 10022853;
                ClientConfig clientConfig = new ClientConfig();
                clientConfig.setRegion(region);
                clientConfig.setSignExpired(24 * 3600);
                Credentials cred = new Credentials(appId, secretId, secretKey);
                COSClient cosClient = new COSClient(clientConfig, cred);

        	    UploadFileRequest uploadFileRequest = new UploadFileRequest(bucket, videoDst, videoFile);
                uploadFileRequest.setInsertOnly(InsertOnly.OVER_WRITE);
                String uploadFileRet = cosClient.uploadFile(uploadFileRequest);

                Constants.logger.info("upload video to cos|recv:" + uploadFileRet);

                if(coverDst != null) {
        		uploadFileRequest = new UploadFileRequest(bucket, coverDst, coverPath);
                        uploadFileRequest.setInsertOnly(InsertOnly.OVER_WRITE);
                        uploadFileRet = cosClient.uploadFile(uploadFileRequest);
                        Constants.logger.info("upload cover to cos|recv:" + uploadFileRet);
                }

                // 第三步，确认上传
                params = new TreeMap<String, Object>();
                params.put("vodSessionKey", vodSessionKey);
                result = null;
                try {
                        result = module.call("CommitUpload", params);
                } catch (Exception e) {
                        Constants.logger.error("CommitUpload call error:"+e.getMessage());
                }

                json_result = new JSONObject(result);
                Constants.logger.info("CommitUpload|recv:" + json_result);

                return json_result;
        }
}
