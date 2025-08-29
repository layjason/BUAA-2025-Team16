package com.example.lal.service;

import org.springframework.web.multipart.MultipartFile;

public interface OSSService {
    String uploadFile(MultipartFile file, String storagePath);
    public boolean submitFile(String fileName);
    public void deleteFile(String fileName);

    public void rollbackFile(String fileName);
}
