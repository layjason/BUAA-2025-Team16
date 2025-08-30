package com.example.lal.service.impl;

import com.example.lal.mapper.ResourceMapper;
import com.example.lal.mapper.UserMapper;
import com.example.lal.model.domain.DownloadStatistic;
import com.example.lal.model.entity.*;
import com.example.lal.model.exceptions.ResourceException;
import com.example.lal.model.request.resource.GetResourceDetailRequest;
import com.example.lal.model.request.resource.ListResourceByCategoryRequest;
import com.example.lal.model.request.resource.ListResourceByUserIdRequest;
import com.example.lal.model.request.resource.SearchResourceRequest;
import com.example.lal.model.request.resource.UploadResourceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceImplTest {

    @Mock private ResourceMapper resourceMapper;
    @Mock private UserMapper userMapper;

    @InjectMocks private ResourceServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        Field f = ResourceServiceImpl.class.getDeclaredField("resourceSummaryPage");
        f.setAccessible(true);
        f.set(service, new Page<ResourceSummary>());
    }

    // ------------------------------- addResource -------------------------------
    @Test
    @DisplayName("addResource: success with generated id and categories")
    void addResource_success() throws Exception {
        UploadResourceRequest req = new UploadResourceRequest();
        req.setTitle("Doc A");
        req.setSubject(1);
        req.setContent("hello");
        req.setFilePath("/tmp/a.pdf");
        req.setImagePath("/img/a.png");
        req.setSize(12345L);
        req.setFileName("a.pdf");
        req.setCategories(new int[]{10, 20});

        // Simulate MyBatis setting the id on insert
        doAnswer(inv -> {
            com.example.lal.model.domain.Resource r = inv.getArgument(0);
            r.setId(101);
            return 1;
        }).when(resourceMapper).createResource(any());

        boolean ok = service.addResource("10000001", req);
        assertTrue(ok);
        verify(resourceMapper).createResource(any());
        verify(resourceMapper).createResourceCategories(101, 10);
        verify(resourceMapper).createResourceCategories(101, 20);
    }

    @Test
    @DisplayName("addResource: mapper throws -> bubbles up (negative)")
    void addResource_mapperThrows() {
        UploadResourceRequest req = new UploadResourceRequest();
        req.setTitle("Doc B");
        req.setSubject(2);
        req.setContent("content");
        req.setFilePath("/f");
        req.setImagePath("/i");
        req.setFileName("b.pdf");
        req.setSize(1L);
        req.setCategories(new int[]{1});

        doThrow(new RuntimeException("DB down")).when(resourceMapper).createResource(any());
        assertThrows(RuntimeException.class, () -> service.addResource("9999999", req));
    }

    // ------------------------------- getResourceDetail -------------------------------
    @Test
    @DisplayName("getResourceDetail: success enriches user + categories")
    void getResourceDetail_success() throws Exception {
        GetResourceDetailRequest req = new GetResourceDetailRequest();
        req.setResourceId("7");

        ResourceDetail rd = new ResourceDetail();
        rd.setId(7);
        rd.setUserId("$1");
        rd.setFileName("x.pdf");

        when(resourceMapper.readResource("7")).thenReturn(rd);
        when(resourceMapper.readResourceCategories("7")).thenReturn(new int[]{3, 4});

        UserDetail u = new UserDetail();
        u.setName("Alice");
        u.setProfilePhotoUrl("/u.png");
        when(userMapper.getUserById("$1")).thenReturn(u);

        ResourceDetail out = service.getResourceDetail(req);
        assertEquals(7, out.getId());
        assertArrayEquals(new int[]{3, 4}, out.getCategories());
        assertEquals("Alice", out.getUsername());
        assertEquals("/u.png", out.getProfilePhotoUrl());
    }

    @Test
    @DisplayName("getResourceDetail: not found -> ResourceException (negative)")
    void getResourceDetail_notFound() {
        GetResourceDetailRequest req = new GetResourceDetailRequest();
        req.setResourceId("8");
        when(resourceMapper.readResource("8")).thenReturn(null);
        assertThrows(ResourceException.class, () -> service.getResourceDetail(req));
    }

    // ------------------------------- updateResourceDetail -------------------------------
    @Test
    @DisplayName("updateResourceDetail: calls mapper")
    void updateResourceDetail_success() {
        ResourceDetail rd = new ResourceDetail();
        rd.setId(1);
        service.updateResourceDetail(rd);
        verify(resourceMapper).updateResource(rd);
    }

    @Test
    @DisplayName("updateResourceDetail: mapper throws (negative)")
    void updateResourceDetail_mapperThrows() {
        doThrow(new RuntimeException("bad")).when(resourceMapper).updateResource(any());
        assertThrows(RuntimeException.class, () -> service.updateResourceDetail(new ResourceDetail()));
    }

    // ------------------------------- updateResourceCategories -------------------------------
    @Test
    @DisplayName("updateResourceCategories: delete then create per category")
    void updateResourceCategories_normal() {
        InOrder inOrder = inOrder(resourceMapper);
        service.updateResourceCategories(9, new int[]{1, 2});
        inOrder.verify(resourceMapper).deleteResourceCategories(9);
        inOrder.verify(resourceMapper).createResourceCategories(9, 1);
        inOrder.verify(resourceMapper).createResourceCategories(9, 2);
    }

    @Test
    @DisplayName("updateResourceCategories: null categories -> NPE (negative)")
    void updateResourceCategories_null() {
        assertThrows(NullPointerException.class, () -> service.updateResourceCategories(9, null));
    }

    // ------------------------------- getResourceSummaryByClassWithPage -------------------------------
    @Test
    @DisplayName("getResourceSummaryByClassWithPage: success populates page + enriches items")
    void getResourceSummaryByClassWithPage_success() {
        ListResourceByCategoryRequest req = new ListResourceByCategoryRequest();
        req.setPageNum(2);
        req.setCntInPage(3);
        req.setSubjects(new int[]{1,2});
        req.setCategories(new int[]{10});

        when(resourceMapper.getResourceBySubjectsAndCategoriesCount(any(int[].class), any(int[].class)))
                .thenReturn(7);

        ResourceSummary s1 = rs(100, 11);
        ResourceSummary s2 = rs(101, 12);
        when(resourceMapper.getResourcesBySubjectAndCategoriesByPage(any(int[].class), any(int[].class), eq(3), eq(3)))
                .thenReturn(Arrays.asList(s1, s2));

        when(userMapper.getUserById(anyString())).thenAnswer(a -> {
            String idStr = a.getArgument(0);
            int id = Integer.parseInt(idStr);
            UserDetail ud = new UserDetail();
            ud.setId(id);
            ud.setName("U" + idStr);
            ud.setProfilePhotoUrl("/u" + idStr + ".png");
            return ud;
        });

        when(resourceMapper.getResourceCategoriesById(100)).thenReturn(catList(1, 2));
        when(resourceMapper.getResourceCategoriesById(101)).thenReturn(catList(3));

        Page<ResourceSummary> page = service.getResourceSummaryByClassWithPage(req);
        assertEquals(7, page.getCount());
        assertEquals(2, page.getCurrPage());
        assertEquals(3, page.getPageSize());
        assertEquals(3, page.getPageCount()); // ceil(7/3) = 3
        assertEquals(3, page.getFilterCount());
        assertEquals(3, page.getNextPage());
        assertEquals(1, page.getPrePage());

        Map<Integer, String> nameById = new HashMap<>();
        for (ResourceSummary r : page.getList()) {
            nameById.put(r.getId(), r.getUserName());
        }
        assertEquals("U11", nameById.get(100));
        assertEquals("U12", nameById.get(101));

        ResourceSummary r100 = page.getList().stream()
                .filter(r -> r.getId() == 100)
                .findFirst().orElseThrow();
        assertArrayEquals(new int[]{1, 2}, r100.getCategories());
    }

    @Test
    @DisplayName("getResourceSummaryByClassWithPage: empty result")
    void getResourceSummaryByClassWithPage_empty() {
        ListResourceByCategoryRequest req = new ListResourceByCategoryRequest();
        req.setPageNum(1);
        req.setCntInPage(5);
        req.setSubjects(new int[]{9});
        req.setCategories(new int[]{7});

        when(resourceMapper.getResourceBySubjectsAndCategoriesCount(any(int[].class), any(int[].class)))
                .thenReturn(0);
        when(resourceMapper.getResourcesBySubjectAndCategoriesByPage(any(int[].class), any(int[].class), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        Page<ResourceSummary> page = service.getResourceSummaryByClassWithPage(req);
        assertEquals(0, page.getCount());
        assertEquals(0, page.getList().size());
    }

    // ------------------------------- deleteResource -------------------------------
    @Test
    @DisplayName("deleteResource: valid id -> calls both mappers")
    void deleteResource_success() {
        service.deleteResource("55");
        verify(resourceMapper).deleteResourceCategories(55);
        verify(resourceMapper).deleteResource("55");
    }

    @Test
    @DisplayName("deleteResource: invalid id -> NumberFormatException (negative)")
    void deleteResource_invalidId() {
        assertThrows(NumberFormatException.class, () -> service.deleteResource("abc"));
    }

    // ------------------------------- getResourceNumByCategoryAndSubject -------------------------------
    @Test
    @DisplayName("getResourceNumByCategoryAndSubject: returns value")
    void getResourceNumByCategoryAndSubject_positive() {
        when(resourceMapper.getResourceNumByCategoryAndSubject(3, 2)).thenReturn(9);
        assertEquals(9, service.getResourceNumByCategoryAndSubject(3, 2));
    }

    @Test
    @DisplayName("getResourceNumByCategoryAndSubject: zero for none (negative)")
    void getResourceNumByCategoryAndSubject_zero() {
        when(resourceMapper.getResourceNumByCategoryAndSubject(99, 88)).thenReturn(0);
        assertEquals(0, service.getResourceNumByCategoryAndSubject(99, 88));
    }

    // ------------------------------- getResourceCountByTime -------------------------------
    @Test
    @DisplayName("getResourceCountByTime: returns value")
    void getResourceCountByTime_positive() {
        LocalDateTime s = LocalDateTime.now().minusDays(7);
        LocalDateTime e = LocalDateTime.now();
        when(resourceMapper.getResourceCountByTime(s, e)).thenReturn(4);
        assertEquals(4, service.getResourceCountByTime(s, e));
    }

    @Test
    @DisplayName("getResourceCountByTime: zero (negative)")
    void getResourceCountByTime_zero() {
        LocalDateTime s = LocalDateTime.now().minusDays(1);
        LocalDateTime e = LocalDateTime.now();
        when(resourceMapper.getResourceCountByTime(s, e)).thenReturn(0);
        assertEquals(0, service.getResourceCountByTime(s, e));
    }

    // ------------------------------- getResourceCountBySubject -------------------------------
    @Test
    @DisplayName("getResourceCountBySubject: returns value")
    void getResourceCountBySubject_positive() {
        when(resourceMapper.getResourceCountBySubject(6)).thenReturn(12);
        assertEquals(12, service.getResourceCountBySubject(6));
    }

    @Test
    @DisplayName("getResourceCountBySubject: zero (negative)")
    void getResourceCountBySubject_zero() {
        when(resourceMapper.getResourceCountBySubject(123)).thenReturn(0);
        assertEquals(0, service.getResourceCountBySubject(123));
    }

    // ------------------------------- searchResource -------------------------------
    @Test
    @DisplayName("searchResource: with keywords -> search path")
    void searchResource_withKeywords() {
        SearchResourceRequest req = new SearchResourceRequest();
        req.setPageNum(1);
        req.setCntInPage(2);
        req.setKeywords("math");
        req.setSubjects(new int[]{1});
        req.setCategories(new int[]{3});

        when(resourceMapper.searchResourceBySubjectsAndCategoriesCount(eq("math"), any(int[].class), any(int[].class)))
                .thenReturn(5);

        ResourceSummary s1 = rs(1, 2);
        ResourceSummary s2 = rs(2, 3);
        when(resourceMapper.searchResourcesBySubjectAndCategoriesByPage(eq("math"), any(int[].class), any(int[].class), eq(0), eq(2)))
                .thenReturn(Arrays.asList(s1, s2));

        when(userMapper.getUserById(anyString()))
                .thenAnswer(a -> {
                    String s = a.getArgument(0);
                    int id = Integer.parseInt(s);
                    return user(id, "U" + s, "/u.png");
                });
        when(resourceMapper.getResourceCategoriesById(anyInt())).thenReturn(catList(9));

        Page<ResourceSummary> page = service.searchResource(req);
        assertEquals(5, page.getCount());
        assertEquals(2, page.getList().size());
        assertEquals("U2", page.getList().get(0).getUserName());
        assertArrayEquals(new int[]{9}, page.getList().get(0).getCategories());
    }

    @Test
    @DisplayName("searchResource: empty keywords -> fallback path (negative)")
    void searchResource_withoutKeywords() {
        SearchResourceRequest req = new SearchResourceRequest();
        req.setPageNum(1);
        req.setCntInPage(1);
        req.setKeywords("");
        req.setSubjects(new int[]{7});
        req.setCategories(new int[]{8});

        when(resourceMapper.getResourceBySubjectsAndCategoriesCount(any(int[].class), any(int[].class))).thenReturn(0);
        when(resourceMapper.getResourcesBySubjectAndCategoriesByPage(any(int[].class), any(int[].class), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        Page<ResourceSummary> page = service.searchResource(req);
        assertEquals(0, page.getCount());
        assertTrue(page.getList().isEmpty());
    }

    // ------------------------------- getResourceSummary -------------------------------
    @Test
    @DisplayName("getResourceSummary: success enriches fields")
    void getResourceSummary_success() {
        ResourceSummary rs = rs(77, 55);
        when(resourceMapper.getResourceSummary(77)).thenReturn(rs);
        when(userMapper.getUserById("55")).thenReturn(user(55, "Ann", "/a.png"));
        when(resourceMapper.getResourceCategoriesById(77)).thenReturn(catList(1, 2, 3));

        ResourceSummary out = service.getResourceSummary(77);
        assertEquals("Ann", out.getUserName());
        assertArrayEquals(new int[]{1, 2, 3}, out.getCategories());
    }

    @Test
    @DisplayName("getResourceSummary: mapper returns null -> NPE (negative)")
    void getResourceSummary_nullSummary() {
        when(resourceMapper.getResourceSummary(999)).thenReturn(null);
        assertThrows(NullPointerException.class, () -> service.getResourceSummary(999));
    }

    // ------------------------------- getResourceSummaryListRandom -------------------------------
    @Test
    @DisplayName("getResourceSummaryListRandom: success enriches list")
    void getResourceSummaryListRandom_success() {
        ResourceSummary a = rs(1, 10);
        ResourceSummary b = rs(2, 11);
        when(resourceMapper.getResourceSummaryListRandom(2, Arrays.asList(1,2)))
                .thenReturn(Arrays.asList(a, b));
        when(userMapper.getUserById("10")).thenReturn(user(10, "U10", "/p10"));
        when(userMapper.getUserById("11")).thenReturn(user(11, "U11", "/p11"));
        when(resourceMapper.getResourceCategoriesById(1)).thenReturn(catList(5));
        when(resourceMapper.getResourceCategoriesById(2)).thenReturn(catList(6));

        List<ResourceSummary> out = service.getResourceSummaryListRandom(2, Arrays.asList(1,2));
        assertEquals(2, out.size());
        assertEquals("U10", out.get(0).getUserName());
        assertArrayEquals(new int[]{5}, out.get(0).getCategories());
    }

    @Test
    @DisplayName("getResourceSummaryListRandom: empty list (negative)")
    void getResourceSummaryListRandom_empty() {
        when(resourceMapper.getResourceSummaryListRandom(3, Collections.emptyList()))
                .thenReturn(Collections.emptyList());
        List<ResourceSummary> out = service.getResourceSummaryListRandom(3, Collections.emptyList());
        assertTrue(out.isEmpty());
    }

    // ------------------------------- getResourceSummaryByUserIdWithPage -------------------------------
    @Test
    @DisplayName("getResourceSummaryByUserIdWithPage: success paginates + enriches")
    void getResourceSummaryByUserIdWithPage_success() {
        ListResourceByUserIdRequest req = new ListResourceByUserIdRequest();
        req.setUserId("42");
        req.setPageNum(1);
        req.setCntInPage(2);

        when(resourceMapper.getResourceCountByUserId(42)).thenReturn(3);

        ResourceSummary s1 = rs(5, 42);
        ResourceSummary s2 = rs(6, 42);
        when(resourceMapper.getResourceSummaryByUserId(eq(42), eq(0), eq(2)))
                .thenReturn(Arrays.asList(s1, s2));

        when(userMapper.getUserById("42")).thenReturn(user(42, "Zoe", "/z.png"));
        when(resourceMapper.getResourceCategoriesById(anyInt())).thenReturn(catList(1));

        Page<ResourceSummary> page = service.getResourceSummaryByUserIdWithPage(req);
        assertEquals(3, page.getCount());
        assertEquals(2, page.getList().size());
        assertEquals("Zoe", page.getList().get(0).getUserName());
    }

    @Test
    @DisplayName("getResourceSummaryByUserIdWithPage: empty (negative)")
    void getResourceSummaryByUserIdWithPage_empty() {
        ListResourceByUserIdRequest req = new ListResourceByUserIdRequest();
        req.setUserId("7");
        req.setPageNum(3);
        req.setCntInPage(5);

        when(resourceMapper.getResourceCountByUserId(7)).thenReturn(0);
        when(resourceMapper.getResourceSummaryByUserId(eq(7), anyInt(), anyInt())).thenReturn(Collections.emptyList());

        Page<ResourceSummary> page = service.getResourceSummaryByUserIdWithPage(req);
        assertEquals(0, page.getCount());
        assertTrue(page.getList().isEmpty());
    }

    // ------------------------------- getAllResourceCount -------------------------------
    @Test
    @DisplayName("getAllResourceCount: returns value")
    void getAllResourceCount_positive() {
        when(resourceMapper.getAllResourceCount()).thenReturn(77);
        assertEquals(77, service.getAllResourceCount());
    }

    @Test
    @DisplayName("getAllResourceCount: zero (negative)")
    void getAllResourceCount_zero() {
        when(resourceMapper.getAllResourceCount()).thenReturn(0);
        assertEquals(0, service.getAllResourceCount());
    }

    // ------------------------------- helpers -------------------------------
    private static ResourceSummary rs(int id, int userId) {
        ResourceSummary s = new ResourceSummary();
        s.setId(id);
        s.setUserId(String.valueOf(userId));
        s.setTitle("T"+id);
        return s;
    }

    private static UserDetail user(int id, String name, String avatar) {
        UserDetail u = new UserDetail();
        u.setId(id);
        u.setName(name);
        u.setProfilePhotoUrl(avatar);
        return u;
    }

    private static List<ResourceCategoryEntry> catList(int... cats) {
        List<ResourceCategoryEntry> list = new ArrayList<>();
        for (int c : cats) {
            ResourceCategoryEntry e = new ResourceCategoryEntry();
            e.setCategory(c);
            list.add(e);
        }
        return list;
    }
}
