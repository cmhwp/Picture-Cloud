package com.photo.pcloudbackend.common;

import lombok.Data;

/**
 * 通用分页请求
 */
@Data
public class PageRequest {
    //当前页号
    private int currentPageNum = 1;
    //每页显示的记录数
    private int pageSize = 10;
    //排序字段
    private String sortField;
    //排序方式
    private String sortOrder ="descend";
}
