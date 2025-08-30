package com.example.lal.Controller;

import com.example.lal.model.RestBean;
import com.example.lal.model.entity.*;
import com.example.lal.model.exceptions.ResourceException;
import com.example.lal.model.request.resource.*;
import com.example.lal.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ResourceControllerTest {

    private ResourceController controller;

    @Mock private ResourceService resourceService;
    @Mock private ExperienceService experienceService;
    @Mock private OSSService ossService;
    @Mock private UserService userService;
    @Mock private DownloadHistoryService downloadHistoryService;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        controller = new ResourceController();
        inject("resourceService", resourceService);
        inject("experienceService", experienceService);
        inject("ossService", ossService);
        inject("userService", userService);
        inject("downloadHistoryService", downloadHistoryService);
    }

    private void inject(String field, Object value) throws Exception {
        var f = ResourceController.class.getDeclaredField(field);
        f.setAccessible(true);
        f.set(controller, value);
    }

    // ---------- uploadFile ----------
    @Test
    void uploadFile_success() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(123L);
        doReturn("oss://resource/file.pdf").when(ossService).uploadFile(eq(file), eq("resource/file"));

        ResponseEntity<RestBean> resp = controller.uploadFile(file, mock(HttpServletRequest.class));
        assertEquals(201, resp.getStatusCode().value());
        verify(ossService).uploadFile(eq(file), eq("resource/file"));
    }

    @Test
    void uploadFile_badRequest_whenEmpty() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(0L);

        ResponseEntity<RestBean> resp = controller.uploadFile(file, mock(HttpServletRequest.class));
        assertEquals(400, resp.getStatusCode().value());
        verifyNoInteractions(ossService);
    }

    // ---------- uploadImage ----------
    @Test
    void uploadImage_success() {
        MultipartFile img = mock(MultipartFile.class);
        when(img.getSize()).thenReturn(99L);
        doReturn("oss://resource/image.png").when(ossService).uploadFile(eq(img), eq("resource/image"));

        ResponseEntity<RestBean> resp = controller.uploadImage(img, mock(HttpServletRequest.class));
        assertEquals(201, resp.getStatusCode().value());
        verify(ossService).uploadFile(eq(img), eq("resource/image"));
    }

    // ---------- uploadResource (avoid static TextCensor by taking error branch) ----------
    @Test
    void uploadResource_missingTitle_returnsBadRequest() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn("10000001");

        UploadResourceRequest ur = new UploadResourceRequest();
        ur.setTitle(""); // triggers "未填写标题" branch before TextCensor
        ur.setContent("whatever");
        ur.setImagePath("oss://img.png");
        ur.setFilePath("oss://file.pdf");

        ResponseEntity<RestBean> resp = controller.uploadResource(ur, request);
        assertEquals(400, resp.getStatusCode().value());
        verifyNoInteractions(resourceService);
    }

    // ---------- getResDetail ----------
    @Test
    void getResDetail_success_noTokenPath() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("token")).thenReturn(null); // avoid JwtUtil static path

        GetResourceDetailRequest req = new GetResourceDetailRequest();
        req.setResourceId("7");

        ResourceDetail rd = new ResourceDetail();
        rd.setId(7);
        rd.setUserId("10000001");
        rd.setFileName("a.pdf");
        doReturn(rd).when(resourceService).getResourceDetail(any(GetResourceDetailRequest.class));

        ResponseEntity<RestBean> resp = controller.getResourceDetail(req, request);
        assertEquals(200, resp.getStatusCode().value());
        verify(resourceService).getResourceDetail(any(GetResourceDetailRequest.class));
    }

    // ---------- updateResource (avoid static TextCensor by taking error branch) ----------
    @Test
    void updateResource_missingTitle_returnsBadRequest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn("10000001");

        UpdateResourceRequest ur = new UpdateResourceRequest();
        ur.setId("77");
        ur.setTitle(""); // empty -> "未填写标题" branch before TextCensor
        ur.setContent("C");
        ur.setSubject(1);
        ur.setCategories(new int[]{2,3});

        ResourceDetail existing = new ResourceDetail();
        existing.setId(77);
        existing.setUserId("10000001");
        doReturn(existing).when(resourceService).getResourceDetail(any(GetResourceDetailRequest.class));

        ResponseEntity<RestBean> resp = controller.updateResource(ur, request);
        assertEquals(400, resp.getStatusCode().value());
        verify(resourceService, never()).updateResourceDetail(any());
        verify(resourceService, never()).updateResourceCategories(anyInt(), any());
    }

    // ---------- deleteResource ----------
    @Test
    void deleteResource_success() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn("10000001");

        DeleteResourceRequest dr = new DeleteResourceRequest();
        dr.setResourceId("9");

        ResourceDetail rd = new ResourceDetail();
        rd.setId(9);
        rd.setUserId("10000001");
        doReturn(rd).when(resourceService).getResourceDetail(any(GetResourceDetailRequest.class));

        ResponseEntity<RestBean> resp = controller.deleteResource(dr, request);
        assertEquals(200, resp.getStatusCode().value());
        verify(resourceService).deleteResource("9");
        verify(experienceService).changeExp("10000001", -5);
    }

    // ---------- listResByClassWithPage ----------
    @Test
    void listResByClassWithPage_success_noToken_canDeleteFalse() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("token")).thenReturn(null); // avoid JwtUtil

        ListResourceByCategoryRequest body = new ListResourceByCategoryRequest();
        body.setCntInPage(2);
        body.setPageNum(1);
        body.setSubjects(new int[]{1});
        body.setCategories(new int[]{2});

        Page<ResourceSummary> page = new Page<>();
        page.setList(Arrays.asList(summary(100, "10000001"), summary(101, "10000002")));
        doReturn(page).when(resourceService).getResourceSummaryByClassWithPage(any(ListResourceByCategoryRequest.class));

        ResponseEntity<RestBean> resp = controller.listResourceByClassWithPage(body, request);
        assertEquals(200, resp.getStatusCode().value());

        for (ResourceSummary rs : page.getList()) {
            assertFalse(rs.isCanDelete());
        }
    }

    // ---------- downloadResource ----------
    @Test
    void downloadResource_success() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn("10000001");

        DownloadResourceRequest dr = new DownloadResourceRequest();
        dr.setResourceId("5");

        ResourceDetail rd = new ResourceDetail();
        rd.setId(5);
        rd.setUserId("10000002");
        rd.setTitle("Doc");
        rd.setFileName("x.pdf");
        rd.setPath("oss://file.pdf");
        rd.setDownloadCount(7);

        doReturn(rd).when(resourceService).getResourceDetail(any(GetResourceDetailRequest.class));
        when(downloadHistoryService.addDownloadHistory("10000001", "5", "Doc", "x.pdf")).thenReturn(true);

        ResponseEntity<RestBean> resp = controller.downloadResource(dr, request);
        assertEquals(200, resp.getStatusCode().value());

        verify(resourceService).updateResourceDetail(argThat(r -> r.getId()==5 && r.getDownloadCount()==8));
        verify(downloadHistoryService).addDownloadHistory("10000001", "5", "Doc", "x.pdf");
        verify(experienceService).changeExp("10000002", 2);
    }

    // ---------- listDownloadHistoryByUserId ----------
    @Test
    void listDownloadHistoryByUserId_success() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn("10000001");

        List<DownloadHistoryEntry> list = new ArrayList<>();
        list.add(new DownloadHistoryEntry());
        when(downloadHistoryService.listDownloadHistoryByUserId("10000001")).thenReturn(list);

        ResponseEntity<RestBean> resp = controller.listDownloadHistoryByUserId(request);
        assertEquals(200, resp.getStatusCode().value());
        verify(downloadHistoryService).listDownloadHistoryByUserId("10000001");
    }

    // ---------- searchResource ----------
    @Test
    void searchResource_success_noToken() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("token")).thenReturn(null);

        SearchResourceRequest body = new SearchResourceRequest();
        body.setCntInPage(2);
        body.setPageNum(1);
        body.setKeywords("math");
        body.setSubjects(new int[]{1});
        body.setCategories(new int[]{3});

        Page<ResourceSummary> page = new Page<>();
        page.setList(Arrays.asList(summary(1, "10000001"), summary(2, "10000002")));
        doReturn(page).when(resourceService).searchResource(any(SearchResourceRequest.class));

        ResponseEntity<RestBean> resp = controller.searchResource(body, request);
        assertEquals(200, resp.getStatusCode().value());
        for (ResourceSummary rs : page.getList()) {
            assertFalse(rs.isCanDelete());
        }
    }

    // ---------- listResourceByUserId ----------
    @Test
    void listResourceByUserId_self_canDeleteTrue() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getAttribute("userId")).thenReturn("42");

        ListResourceByUserIdRequest body = new ListResourceByUserIdRequest();
        body.setUserId("42");
        body.setCntInPage(2);
        body.setPageNum(1);

        Page<ResourceSummary> page = new Page<>();
        page.setList(Arrays.asList(summary(10, "42"), summary(11, "42")));
        doReturn(page).when(resourceService).getResourceSummaryByUserIdWithPage(any(ListResourceByUserIdRequest.class));

        ResponseEntity<RestBean> resp = controller.listResourceByUserId(body, request);
        assertEquals(200, resp.getStatusCode().value());
        for (ResourceSummary rs : page.getList()) {
            assertTrue(rs.isCanDelete());
        }
    }

    // ---------- helpers ----------
    private static ResourceSummary summary(int id, String userId) {
        ResourceSummary rs = new ResourceSummary();
        rs.setId(id);
        rs.setUserId(userId);
        rs.setTitle("T"+id);
        return rs;
    }
}

