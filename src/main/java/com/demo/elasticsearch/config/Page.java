package com.demo.elasticsearch.config;

import lombok.Data;

import java.util.List;

/**
 * @Author jky
 * @Time 2019/8/5 10:49
 * @Description
 */
@Data
public class Page<T> {

    private int pageSize;//页大小

    private int pageNum;//页数

    private int totalPage;//总共的页数

    private int totalNum;//总共的实体数量

    private List<T> entities;


    public int getFromNum() {
        return pageSize * (pageNum - 1);
    }

    public void calculate() {
        this.totalPage = totalNum % pageSize > 0 ? (totalNum / pageSize) + 1 : (totalNum / pageSize);
    }


    public boolean isLastPage() {
        return pageNum == totalPage;
    }


    public void nextPage() {
        this.pageNum++;
        this.entities = null;
    }

}
