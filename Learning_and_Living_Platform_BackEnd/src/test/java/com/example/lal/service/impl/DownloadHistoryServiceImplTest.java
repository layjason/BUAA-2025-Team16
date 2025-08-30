package com.example.lal.service.impl;

import com.example.lal.mapper.DownloadHistoryMapper;
import com.example.lal.model.entity.DownloadHistoryEntry;
import com.example.lal.model.entity.UserResourceMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DownloadHistoryServiceImplTest {

    @Mock
    private DownloadHistoryMapper downloadHistoryMapper;

    @InjectMocks
    private DownloadHistoryServiceImpl downloadHistoryService;

    @BeforeEach
    public void setup() {
        // Setup mocks if needed
    }

    // Test for getDownloadsByTime
    @Test
    public void testGetDownloadsByTime() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();

        // Mock the mapper's response
        when(downloadHistoryMapper.getDownloadsByTime(start, end)).thenReturn(10);

        int result = downloadHistoryService.getDownloadsByTime(start, end);
        assertEquals(10, result);
        verify(downloadHistoryMapper, times(1)).getDownloadsByTime(start, end);
    }

    // Test for addDownloadHistory
    @Test
    public void testAddDownloadHistory() {
        String userId = "1001";
        String resourceId = "2001";
        String resourceTitle = "Test Resource";
        String fileName = "testfile.txt";

        DownloadHistoryEntry entry = new DownloadHistoryEntry();
        entry.setUserId(Integer.parseInt(userId));
        entry.setResourceId(Integer.parseInt(resourceId));
        entry.setTitle(resourceTitle);
        entry.setFileName(fileName);
        entry.setDownloadTime(new java.sql.Date(System.currentTimeMillis()));

        // Mock the insert behavior
        when(downloadHistoryMapper.createDownloadHistory(any(DownloadHistoryEntry.class))).thenReturn(true);

        boolean result = downloadHistoryService.addDownloadHistory(userId, resourceId, resourceTitle, fileName);
        assertTrue(result);
        verify(downloadHistoryMapper, times(1)).createDownloadHistory(any(DownloadHistoryEntry.class));
    }

    // Test for listDownloadHistoryByUserId
    @Test
    public void testListDownloadHistoryByUserId() {
        String userId = "1001";
        List<DownloadHistoryEntry> mockList = new ArrayList<>();
        DownloadHistoryEntry entry = new DownloadHistoryEntry();
        entry.setUserId(Integer.parseInt(userId));
        mockList.add(entry);

        when(downloadHistoryMapper.readDownloadHistoryByUserId(Integer.parseInt(userId))).thenReturn(mockList);

        List<DownloadHistoryEntry> result = downloadHistoryService.listDownloadHistoryByUserId(userId);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    // Test for listAllResourceId
    @Test
    public void testListAllResourceId() {
        List<Integer> mockResourceIds = Arrays.asList(1, 2, 3);

        when(downloadHistoryMapper.readAllResourceId()).thenReturn(mockResourceIds);

        List<Integer> result = downloadHistoryService.listAllResourceId();
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    // Test for listDownloadHistoryUserIdByResourceId
    @Test
    public void testListDownloadHistoryUserIdByResourceId() {
        Integer resourceId = 1001;
        List<Integer> mockUserIds = Arrays.asList(1, 2, 3);

        when(downloadHistoryMapper.readDownloadHistoryByResourceId(resourceId)).thenReturn(mockUserIds);

        List<Integer> result = downloadHistoryService.listDownloadHistoryUserIdByResourceId(resourceId);
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    // Test for getUserResourceMap
    @Test
    public void testGetUserResourceMap() {
        List<UserResourceMap> mockMapList = Arrays.asList(
                new UserResourceMap(1, 5),
                new UserResourceMap(2, 10));

        when(downloadHistoryMapper.getUserResourceMap()).thenReturn(mockMapList);

        Map<Integer, Integer> result = downloadHistoryService.getUserResourceMap();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(5, result.get(1));
    }
}
