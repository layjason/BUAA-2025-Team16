package com.example.lal.model.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
@Data
@Component
public class Page<E> {
    //当前页码
    private int currPage;
    //上一页
    private int prePage;
    //下一页
    private int nextPage;
    //查询的数据总条数
    private int count;
    //当前页的数据内容
    private List<E> list;
    //每页的数据条数
    private int pageSize;
    //总页数
    private int pageCount;
    //过滤条数
    private int filterCount;
}
