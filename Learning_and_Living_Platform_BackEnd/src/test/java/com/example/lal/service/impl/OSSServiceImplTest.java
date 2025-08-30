package com.example.lal.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.example.lal.config.OSSConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OSSServiceImplTest {

    private static final String CLOUD =
            "https://learning-and-living.oss-cn-beijing.aliyuncs.com/";

    @Mock private OSS ossClient;

    @InjectMocks private OSSServiceImpl service;

    @BeforeEach
    void setup() throws Exception {
        OSSConfiguration realCfg = new OSSConfiguration();
        setPrivateField(realCfg, "bucketName", "bucket");
        setPrivateField(service, "ossConfiguration", realCfg);
    }

    // ------------------------ tests ------------------------

    @Test
    void uploadFile_success() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("abc".getBytes()));
        when(file.getContentType()).thenReturn("text/plain");

        String url = service.uploadFile(file, "resource/file");

        assertNotNull(url);
        assertTrue(url.startsWith(CLOUD + "resource/file/"));

        verify(ossClient).putObject(
                eq("bucket"),
                argThat(s -> s.startsWith("resource/file/")),
                any(InputStream.class),
                any(ObjectMetadata.class)
        );
    }

    @Test
    void uploadFile_ioException_returnsNull_andDoesNotPut() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenThrow(new IOException("boom"));

        String url = service.uploadFile(file, "resource/file");
        assertNull(url);

        verify(ossClient, never()).putObject(
                anyString(),
                anyString(),
                any(InputStream.class),
                any(ObjectMetadata.class)
        );
    }

    @Test
    void submitFile_true_afterUpload() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("xyz".getBytes()));
        when(file.getContentType()).thenReturn("application/pdf");

        String url = service.uploadFile(file, "resource/file");
        assertNotNull(url);

        assertTrue(service.submitFile(url));
    }

    @Test
    void submitFile_false_whenNotInMapOrWrongPrefix() {
        assertFalse(service.submitFile(CLOUD + "resource/file/not-exists"));
        assertFalse(service.submitFile("resource/file/not-exists")); // wrong prefix
    }

    @Test
    void deleteFile_withFullUrl_deletesAndRemovesFromMap() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("123".getBytes()));
        when(file.getContentType()).thenReturn("application/octet-stream");

        String url = service.uploadFile(file, "resource/file");
        assertNotNull(url);

        service.deleteFile(url);

        verify(ossClient).deleteObject(
                eq("bucket"),
                argThat(key -> key.startsWith("resource/file/"))
        );

        assertFalse(service.submitFile(url));
    }

    @Test
    void rollbackFile_thenSubmit_returnsTrue() {
        String url = CLOUD + "resource/image/some-key";
        service.rollbackFile(url);
        assertTrue(service.submitFile(url));
    }

    // ------------------------ helpers ------------------------

    private static void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, value);
    }
}

