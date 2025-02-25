package com.photo.pcloudbackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

@Data
public class PictureUploadRequest implements Serializable {

    /**
     * 图片 id（用于修改）
     */
    private Long id;

    /**
     * 图片 URL
     */
    private String url;

    /**
     * 图片名称
     */
    private String PicName;

    private static final long serialVersionUID = 1L;
}