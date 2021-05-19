package com.frms.utils;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class OSSClientUtil {
    Log log = LogFactory.getLog(OSSClientUtil.class);

    //阿里云OSS地址，这里看根据你的oss选择（选用自己的）
    protected static String endpoint = "";
    //阿里云OSS账号（选用自己的）
    protected static String accessKeyId  = "";
    //阿里云OSS密钥（选用自己的）
    protected static String accessKeySecret  = "";
    //阿里云OSS上的存储块bucket名字（选用自己的）
    protected static String bucketName  = "";

    private OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

    //阿里云文件缓存目录
    private static String tmpDir = "tmp/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/";
    //阿里云文件目录
    private static String fileDir = "file/" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "/";


    public static void tmpUpload(File tmpFile) throws Exception {
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, tmpDir+tmpFile.getName(), tmpFile);
        ossClient.putObject(putObjectRequest);
        ossClient.shutdown();
    }

    public static void fileUpload(File file) {
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileDir+file.getName(), file);
        ossClient.putObject(putObjectRequest);
        ossClient.shutdown();
    }

    public static String getTmpUrl(String fileName) {
        if(fileName != null){
            String tmpUrl = "https://" + bucketName + "." + endpoint + "/" + tmpDir + fileName;
            return tmpUrl;
        }
        return null;
    }
    public static String getFileUrl(String fileName) {
        if(fileName != null){
            String fileUrl = "https://" + bucketName + "." + endpoint + "/" + fileDir + fileName;
            return fileUrl;
        }
        return null;
    }

    public static void fileDownload(File file) {
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        ossClient.getObject(new GetObjectRequest(bucketName, fileDir+file.getName()));
        ossClient.shutdown();
    }

    public static void tmpDownload(File tmpFile) {
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        ossClient.getObject(new GetObjectRequest(bucketName, tmpDir+tmpFile.getName()));
        ossClient.shutdown();
    }

}
