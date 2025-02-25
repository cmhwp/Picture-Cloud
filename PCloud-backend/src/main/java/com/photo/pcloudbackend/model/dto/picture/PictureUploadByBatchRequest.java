package com.photo.pcloudbackend.model.dto.picture;

import java.io.Serializable;


import lombok.Data;

/**
 * 批量抓取图片请求
 */
@Data
public class PictureUploadByBatchRequest implements Serializable {
    //搜索关键词
    private String searchText;
    //抓取数量
    private Integer fetchCount =10;
    //图片名称前缀
    private String prefixName;
    private static final long serialVersionUID = 1L;
}
