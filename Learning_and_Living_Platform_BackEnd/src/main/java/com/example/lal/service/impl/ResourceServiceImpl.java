package com.example.lal.service.impl;

import com.example.lal.mapper.ResourceMapper;
import com.example.lal.mapper.UserMapper;
import com.example.lal.model.domain.DownloadStatistic;
import com.example.lal.model.entity.*;
import com.example.lal.model.exceptions.ResourceException;
import com.example.lal.model.request.resource.ListResourceByUserIdRequest;
import com.example.lal.model.request.resource.GetResourceDetailRequest;
import com.example.lal.model.request.resource.ListResourceByCategoryRequest;
import com.example.lal.model.request.resource.SearchResourceRequest;
import com.example.lal.model.request.resource.UploadResourceRequest;
import com.example.lal.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {
    @Resource
    private ResourceMapper resourceMapper;
    @Resource
    private UserMapper userMapper;
    @Autowired
    Page<ResourceSummary> resourceSummaryPage;
    @Override
    public boolean addResource(String userId,UploadResourceRequest uploadResourceRequest) throws ResourceException{
        //int userId, String title, Subject subject, Category category, Timestamp publishTime, String content, String path, String imageUr
        if(Integer.parseInt(userId)<10000000)userId= "10000000";
        String title = uploadResourceRequest.getTitle();
        int subject = uploadResourceRequest.getSubject();
        //String category = uploadResourceRequest.getCategory().getValue();
        Timestamp publishTime = new Timestamp(System.currentTimeMillis());
        String content = uploadResourceRequest.getContent();
        String path = uploadResourceRequest.getFilePath();
        String imageUrl = uploadResourceRequest.getImagePath();
        long size = uploadResourceRequest.getSize();

        com.example.lal.model.domain.Resource resource = new com.example.lal.model.domain.Resource();
        resource.setTitle(title);
        resource.setContent(content);
        resource.setSubject(subject);
        resource.setUserId(Integer.parseInt(userId));
        resource.setPublishTime(publishTime);
        resource.setPath(path);
        resource.setImageUrl(imageUrl);
        resource.setSize(size);
        resource.setFileName(uploadResourceRequest.getFileName());

        resourceMapper.createResource(resource);
        //遍历subjects和categories
        int subjects = uploadResourceRequest.getSubject();
        int[] categories = uploadResourceRequest.getCategories();
        for(int category:categories){
            resourceMapper.createResourceCategories(resource.getId(),category);
        }
        return true;
    }

    @Override
    public ResourceDetail getResourceDetail(GetResourceDetailRequest getResourceDetailRequest)throws ResourceException{
        // System.out.println("resource id is"+ getResourceDetailRequest);
        // System.out.println(getResourceDetailRequest.getResourceId());
        ResourceDetail resourceDetail = resourceMapper.readResource(getResourceDetailRequest.getResourceId());
        if(resourceDetail==null){
            System.err.println("null resource detail");
            throw new ResourceException("资源不存在");
        }
        // System.out.println(resourceDetail.getFileName());
        resourceDetail.setCategories(resourceMapper.readResourceCategories(getResourceDetailRequest.getResourceId()));
        UserDetail userDetail = userMapper.getUserById(resourceDetail.getUserId());
        if(userDetail!=null){
            resourceDetail.setUsername(userDetail.getName());
            resourceDetail.setProfilePhotoUrl(userDetail.getProfilePhotoUrl());
        }
        System.out.println(resourceDetail);
        return resourceDetail;
    }

    @Override
    public void updateResourceDetail(ResourceDetail resourceDetail){
        resourceMapper.updateResource(resourceDetail);
    }
    @Override
    public void updateResourceCategories(int resourceId,int[] categories){
        resourceMapper.deleteResourceCategories(resourceId);
        //遍历categories
        for(int category:categories){
            resourceMapper.createResourceCategories(resourceId,category);
        }
    }
    @Override
    public Page<ResourceSummary> getResourceSummaryByClassWithPage(ListResourceByCategoryRequest listResourceByCategoryRequest){
        int currentPage = listResourceByCategoryRequest.getPageNum();
        int pageSize = listResourceByCategoryRequest.getCntInPage();
        int count=0;
        count=resourceMapper.getResourceBySubjectsAndCategoriesCount(listResourceByCategoryRequest.getSubjects(),
                listResourceByCategoryRequest.getCategories());
        //System.out.println(count);
        //数据总条数存入page对象
        resourceSummaryPage.setCount(count);
        //设置当前页数
        resourceSummaryPage.setCurrPage(currentPage);
        //设置每页条数
        resourceSummaryPage.setPageSize(pageSize);
        //设置过滤条数过滤前面页面数据
        resourceSummaryPage.setFilterCount((resourceSummaryPage.getCurrPage()-1)* resourceSummaryPage.getPageSize());
        //设置总页数，总页数+每页条数-1再运算可使总页数向上取整
        resourceSummaryPage.setPageCount((resourceSummaryPage.getCount()+ resourceSummaryPage.getPageSize()-1)/ resourceSummaryPage.getPageSize());
        //设置下一页
        resourceSummaryPage.setNextPage(resourceSummaryPage.getCurrPage()== resourceSummaryPage.getPageCount()? resourceSummaryPage.getCurrPage(): resourceSummaryPage.getCurrPage()+1);
        //设置上一页
        resourceSummaryPage.setPrePage(resourceSummaryPage.getCurrPage()==1? resourceSummaryPage.getCurrPage(): resourceSummaryPage.getCurrPage()-1);
        //System.out.println(resourceSummaryPage.getFilterCount());
        List<ResourceSummary> resourceSummaryList = resourceMapper
                .getResourcesBySubjectAndCategoriesByPage
                        (listResourceByCategoryRequest.getSubjects(),
                                listResourceByCategoryRequest.getCategories(),
                                resourceSummaryPage.getFilterCount(),
                                resourceSummaryPage.getPageSize()
                        );
        for(ResourceSummary resourceSummary:resourceSummaryList){
            UserDetail userDetail = userMapper.getUserById(resourceSummary.getUserId());
            if(userDetail!=null){
                resourceSummary.setUserName(userDetail.getName());
                resourceSummary.setProfilePhotoUrl(userDetail.getProfilePhotoUrl());
            }
            List<ResourceCategoryEntry> resourceCategoryEntryList = resourceMapper.getResourceCategoriesById(resourceSummary.getId());
            resourceSummary.setCategories(new int[resourceCategoryEntryList.size()]);
            for(int i=0;i< resourceCategoryEntryList.size();i++){
                resourceSummary.getCategories()[i] = resourceCategoryEntryList.get(i).getCategory();
            }
        }
        resourceSummaryPage
                .setList(resourceSummaryList);

        return resourceSummaryPage;
        //return null;
    }
    @Override
    public void deleteResource(String resourceId){
        resourceMapper.deleteResourceCategories(Integer.parseInt(resourceId));
        resourceMapper.deleteResource(resourceId);
    }
    @Override
    public int getResourceNumByCategoryAndSubject(int category, int subject){
        return resourceMapper.getResourceNumByCategoryAndSubject(category,subject);
    }

    @Override
    public int getResourceCountByTime(LocalDateTime startTime, LocalDateTime endTime){
        return resourceMapper.getResourceCountByTime(startTime,endTime);
    }

    @Override
    public int getResourceCountBySubject(int subject){
        return resourceMapper.getResourceCountBySubject(subject);
    }

    @Override
    public Page<ResourceSummary> searchResource(SearchResourceRequest searchResourceRequest){
        int currentPage = searchResourceRequest.getPageNum();
        int pageSize = searchResourceRequest.getCntInPage();
        int count=0;

        System.out.println("keywords:"+searchResourceRequest.getKeywords());
        if(searchResourceRequest.getKeywords() == null || searchResourceRequest.getKeywords().equals("")){
            count = resourceMapper.getResourceBySubjectsAndCategoriesCount(searchResourceRequest.getSubjects(),
                    searchResourceRequest.getCategories());
        }else
            count=resourceMapper.searchResourceBySubjectsAndCategoriesCount(searchResourceRequest.getKeywords(),
                searchResourceRequest.getSubjects(),
                searchResourceRequest.getCategories());
        //System.out.println(count);
        //数据总条数存入page对象
        resourceSummaryPage.setCount(count);
        //设置当前页数
        resourceSummaryPage.setCurrPage(currentPage);
        //设置每页条数
        resourceSummaryPage.setPageSize(pageSize);
        //设置过滤条数过滤前面页面数据
        resourceSummaryPage.setFilterCount((resourceSummaryPage.getCurrPage()-1)* resourceSummaryPage.getPageSize());
        //设置总页数，总页数+每页条数-1再运算可使总页数向上取整
        resourceSummaryPage.setPageCount((resourceSummaryPage.getCount()+ resourceSummaryPage.getPageSize()-1)/ resourceSummaryPage.getPageSize());
        //设置下一页
        resourceSummaryPage.setNextPage(resourceSummaryPage.getCurrPage()== resourceSummaryPage.getPageCount()? resourceSummaryPage.getCurrPage(): resourceSummaryPage.getCurrPage()+1);
        //设置上一页
        resourceSummaryPage.setPrePage(resourceSummaryPage.getCurrPage()==1? resourceSummaryPage.getCurrPage(): resourceSummaryPage.getCurrPage()-1);
        //System.out.println(resourceSummaryPage.getFilterCount());
        List<ResourceSummary> resourceSummaryList;
        if(searchResourceRequest.getKeywords() == null || searchResourceRequest.getKeywords().equals("")){
           resourceSummaryList = resourceMapper
                   .getResourcesBySubjectAndCategoriesByPage(
                           searchResourceRequest.getSubjects(),
                           searchResourceRequest.getCategories(),
                           resourceSummaryPage.getFilterCount(),
                           resourceSummaryPage.getPageSize());
        }else{
            resourceSummaryList = resourceMapper
                    .searchResourcesBySubjectAndCategoriesByPage(
                                    searchResourceRequest.getKeywords(),
                                    searchResourceRequest.getSubjects(),
                                    searchResourceRequest.getCategories(),
                                    resourceSummaryPage.getFilterCount(),
                                    resourceSummaryPage.getPageSize()
                            );
        }
        for(ResourceSummary resourceSummary:resourceSummaryList){
            UserDetail userDetail = userMapper.getUserById(resourceSummary.getUserId());
            if(userDetail!=null){
                resourceSummary.setUserName(userDetail.getName());
                resourceSummary.setProfilePhotoUrl(userDetail.getProfilePhotoUrl());
            }
            List<ResourceCategoryEntry> resourceCategoryEntryList = resourceMapper.getResourceCategoriesById(resourceSummary.getId());
            resourceSummary.setCategories(new int[resourceCategoryEntryList.size()]);
            for(int i=0;i< resourceCategoryEntryList.size();i++){
                resourceSummary.getCategories()[i] = resourceCategoryEntryList.get(i).getCategory();
            }
        }
        resourceSummaryPage
                .setList(resourceSummaryList);

        return resourceSummaryPage;
        //return null;
    }
    @Override
    public ResourceSummary getResourceSummary(int resourceId){
        ResourceSummary resourceSummary = resourceMapper.getResourceSummary(resourceId);
        UserDetail userDetail = userMapper.getUserById(resourceSummary.getUserId());
        if(userDetail!=null){
            resourceSummary.setUserName(userDetail.getName());
            resourceSummary.setProfilePhotoUrl(userDetail.getProfilePhotoUrl());
        }
        List<ResourceCategoryEntry> resourceCategoryEntryList = resourceMapper.getResourceCategoriesById(resourceSummary.getId());
        resourceSummary.setCategories(new int[resourceCategoryEntryList.size()]);
        for(int i=0;i< resourceCategoryEntryList.size();i++){
            resourceSummary.getCategories()[i] = resourceCategoryEntryList.get(i).getCategory();
        }
        return resourceSummary;
    }
    @Override
    public List<ResourceSummary> getResourceSummaryListRandom(int size, List<Integer> resourceIDs){
        List<ResourceSummary> resourceSummaryList= resourceMapper.getResourceSummaryListRandom(size,resourceIDs);
        for(ResourceSummary resourceSummary:resourceSummaryList){
            UserDetail userDetail = userMapper.getUserById(resourceSummary.getUserId());
            if(userDetail!=null){
                resourceSummary.setUserName(userDetail.getName());
                resourceSummary.setProfilePhotoUrl(userDetail.getProfilePhotoUrl());
            }
            List<ResourceCategoryEntry> resourceCategoryEntryList = resourceMapper.getResourceCategoriesById(resourceSummary.getId());
            resourceSummary.setCategories(new int[resourceCategoryEntryList.size()]);
            for(int i=0;i< resourceCategoryEntryList.size();i++){
                resourceSummary.getCategories()[i] = resourceCategoryEntryList.get(i).getCategory();
            }
        }
        return  resourceSummaryList;
    }
    @Override
    public Page<ResourceSummary> getResourceSummaryByUserIdWithPage(ListResourceByUserIdRequest listResourceByUserIdRequest){
        int userId =Integer.parseInt(listResourceByUserIdRequest.getUserId());
        int currentPage = listResourceByUserIdRequest.getPageNum();
        int pageSize = listResourceByUserIdRequest.getCntInPage();
        int count=0;

        //System.out.println("keywords:"+searchResourceRequest.getKeywords());
        count = resourceMapper.getResourceCountByUserId(userId);
        //System.out.println(count);
        //数据总条数存入page对象
        resourceSummaryPage.setCount(count);
        //设置当前页数
        resourceSummaryPage.setCurrPage(currentPage);
        //设置每页条数
        resourceSummaryPage.setPageSize(pageSize);
        //设置过滤条数过滤前面页面数据
        resourceSummaryPage.setFilterCount((resourceSummaryPage.getCurrPage()-1)* resourceSummaryPage.getPageSize());
        //设置总页数，总页数+每页条数-1再运算可使总页数向上取整
        resourceSummaryPage.setPageCount((resourceSummaryPage.getCount()+ resourceSummaryPage.getPageSize()-1)/ resourceSummaryPage.getPageSize());
        //设置下一页
        resourceSummaryPage.setNextPage(resourceSummaryPage.getCurrPage()== resourceSummaryPage.getPageCount()? resourceSummaryPage.getCurrPage(): resourceSummaryPage.getCurrPage()+1);
        //设置上一页
        resourceSummaryPage.setPrePage(resourceSummaryPage.getCurrPage()==1? resourceSummaryPage.getCurrPage(): resourceSummaryPage.getCurrPage()-1);
        //System.out.println(resourceSummaryPage.getFilterCount());
        List<ResourceSummary> resourceSummaryList;
        resourceSummaryList = resourceMapper.getResourceSummaryByUserId(userId,
                resourceSummaryPage.getFilterCount(),
                resourceSummaryPage.getPageSize());
        for(ResourceSummary resourceSummary:resourceSummaryList){
            UserDetail userDetail = userMapper.getUserById(resourceSummary.getUserId());
            System.out.println(resourceSummary.getId());
            if(userDetail!=null){
                resourceSummary.setUserName(userDetail.getName());
                resourceSummary.setProfilePhotoUrl(userDetail.getProfilePhotoUrl());
            }
            List<ResourceCategoryEntry> resourceCategoryEntryList = resourceMapper.getResourceCategoriesById(resourceSummary.getId());
            resourceSummary.setCategories(new int[resourceCategoryEntryList.size()]);
            for(int i=0;i< resourceCategoryEntryList.size();i++){
                resourceSummary.getCategories()[i] = resourceCategoryEntryList.get(i).getCategory();
            }
        }
        resourceSummaryPage.setList(resourceSummaryList);
        return resourceSummaryPage;
    }

    @Override
    public int getAllResourceCount(){
        return resourceMapper.getAllResourceCount();
    }
}

