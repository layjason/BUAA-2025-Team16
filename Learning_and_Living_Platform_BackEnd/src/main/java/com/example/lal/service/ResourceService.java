package com.example.lal.service;

import com.example.lal.model.domain.DownloadStatistic;
import com.example.lal.model.request.resource.ListResourceByUserIdRequest;
import com.example.lal.model.entity.Page;
import com.example.lal.model.entity.ResourceDetail;
import com.example.lal.model.entity.ResourceSummary;
import com.example.lal.model.exceptions.ResourceException;
import com.example.lal.model.request.resource.GetResourceDetailRequest;
import com.example.lal.model.request.resource.ListResourceByCategoryRequest;
import com.example.lal.model.request.resource.SearchResourceRequest;
import com.example.lal.model.request.resource.UploadResourceRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface ResourceService {
    boolean addResource(String userId,UploadResourceRequest uploadResourceRequest) throws ResourceException;

    ResourceDetail getResourceDetail(GetResourceDetailRequest getResourceDetailRequest)throws ResourceException;
    Page<ResourceSummary> getResourceSummaryByClassWithPage(ListResourceByCategoryRequest listResourceByCategoryRequest);

    void updateResourceDetail(ResourceDetail resourceDetail);

    void updateResourceCategories(int resourceId, int[] categories);

    void deleteResource(String resourceId);

    int getResourceNumByCategoryAndSubject(int category, int subject);

    int getResourceCountByTime(LocalDateTime startTime, LocalDateTime endTime);

    int getResourceCountBySubject(int subject);


    Page<ResourceSummary> searchResource(SearchResourceRequest searchResourceRequest);


    ResourceSummary getResourceSummary(int resourceId);

    List<ResourceSummary> getResourceSummaryListRandom(int size, List<Integer> resourceIDs);

    int getAllResourceCount();

    Page<ResourceSummary> getResourceSummaryByUserIdWithPage(ListResourceByUserIdRequest listResourceByUserIdRequest);
}
