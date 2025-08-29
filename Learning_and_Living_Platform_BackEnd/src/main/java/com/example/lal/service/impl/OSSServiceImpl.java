package com.example.lal.service.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.example.lal.config.OSSConfiguration;
import com.example.lal.service.OSSService;
import org.apache.tomcat.jni.FileInfo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Component
public class OSSServiceImpl implements OSSService{

    public static Logger log = LoggerFactory.getLogger(OSSService.class);

    @Autowired
    private OSSConfiguration ossConfiguration;

    @Autowired
    private OSS ossClient;


    private static class UploadFileInfo {
        private String fileName;
        private Date uploadTime;
        private boolean submitted;

        public UploadFileInfo(String fileName, Date uploadTime, boolean submitted) {
            this.fileName = fileName;
            this.uploadTime = uploadTime;
            this.submitted = submitted;
        }

        public String getFileName() {
            return fileName;
        }

        public Date getUploadTime() {
            return uploadTime;
        }

        public boolean isSubmitted() {
            return submitted;
        }

        public void setSubmitted(boolean submitted) {
            this.submitted = submitted;
        }
    }

    // 存储上传文件信息的集合
    private ConcurrentMap<String, UploadFileInfo> fileMap = new ConcurrentHashMap<>();
    private String cloudName = "https://learning-and-living.oss-cn-beijing.aliyuncs.com/";

    /**
     * 上传文件到阿里云 OSS 服务器
     * 链接：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/upload_object.html?spm=5176.docoss/user_guide/upload_object
     *
     * @param file
     * @param storagePath
     * @return
     */
    public String uploadFile(MultipartFile file, String storagePath) {
        String fileName = "";

        try {
            // 创建一个唯一的文件名，类似于id，就是保存在OSS服务器上文件的文件名
            fileName = UUID.randomUUID().toString();
            InputStream inputStream = file.getInputStream();
            // 设置对象
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 设置数据流里有多少个字节可以读取
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentDisposition("inline;filename=" + fileName);
            fileName = storagePath + "/" + fileName;
            // 上传文件
            ossClient.putObject(ossConfiguration.getBucketName(), fileName, inputStream, objectMetadata);
            UploadFileInfo fileInfo = new UploadFileInfo(fileName, new Date(), false);
            fileMap.put(fileName, fileInfo);
        } catch (IOException e) {
            log.error("Error occurred: {}", e.getMessage(), e);
            return null;
        }
        return cloudName + fileName;
    }

    // 提交文件处理方法
    public boolean submitFile(String fileName) {
        System.out.println(cloudName);
        if (fileName.startsWith(cloudName)) {
            fileName = fileName.substring(cloudName.length());
        } else {
            return false;
        }
        UploadFileInfo fileInfo = fileMap.get(fileName);
        if (fileInfo != null) {
            fileInfo.setSubmitted(true);
            fileMap.remove(fileName);
            return true;
        } else {
            return false;
        }
    }

    // 定时任务，每分钟检查文件状态并执行删除操作
    @Scheduled(cron ="0 0/10 * * * ?")
    public void checkFileStatus() {
        long currentTime = System.currentTimeMillis();
        System.out.println("currentTime:"+currentTime);

        // 遍历文件集合，检查每个文件的状态
        for (Map.Entry<String, UploadFileInfo> entry : fileMap.entrySet()) {
            String fileName = entry.getKey();
            UploadFileInfo fileInfo = entry.getValue();

            // 判断文件是否超过一分钟且未提交
            if (!fileInfo.isSubmitted() && currentTime - fileInfo.getUploadTime().getTime() >= 60000) {
                System.out.println(fileInfo.getUploadTime().getTime());
                deleteFile(fileName);
            }
        }
    }

    public void deleteFile(String fileName) {
        try {
            System.out.println("fileName is:" + fileName);
            if(fileName != "" && fileName != null){
                System.out.println("IN IT!!!!!");
                if(fileName.contains(cloudName)){
                    fileName = fileName.substring(cloudName.length());
                }
                // 删除文件
                ossClient.deleteObject(ossConfiguration.getBucketName(), fileName);
                if(fileMap.get(fileName) != null){
                    fileMap.remove(fileName);
                }
            }

        } catch (OSSException | ClientException e) {
            log.error("Error occurred while deleting file: {}", e.getMessage(), e);
        }
    }

    @Override
    public void rollbackFile(String fileName){
        if (fileName.startsWith(cloudName)) {
            fileName = fileName.substring(cloudName.length());
        } else {
            return;
        }

        UploadFileInfo fileInfo = fileMap.get(fileName);
        if (fileInfo == null) {
            fileInfo = new UploadFileInfo(fileName, new Date(), false);
            fileMap.put(fileName, fileInfo);
        }
    }
}
