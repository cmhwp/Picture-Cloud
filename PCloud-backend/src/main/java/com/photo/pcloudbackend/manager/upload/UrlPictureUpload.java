package com.photo.pcloudbackend.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.photo.pcloudbackend.exception.BusinessException;
import com.photo.pcloudbackend.exception.ErrorCode;
import com.photo.pcloudbackend.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@Service
public class UrlPictureUpload extends FileManager {
    @Override
    protected void handleInputSource(Object inputSource, File file) throws Exception {
        String url = (String) inputSource;
        int maxRetries = 3;
        int currentTry = 0;
        Exception lastException = null;

        while (currentTry < maxRetries) {
            try {
                // 创建带有完整请求头的GET请求
                HttpResponse response = HttpUtil.createGet(url)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .header("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .header("Connection", "keep-alive")
                    .header("Referer", "https://www.bing.com/")  // 添加 Referer
                    .timeout(10000)  // 设置10秒超时
                    .execute();
                
                if (response.getStatus() == 200) {
                    // 将响应内容写入文件
                    response.writeBody(file);
                    // 验证文件是否成功写入且大小正常
                    if (file.exists() && file.length() > 0) {
                        return;  // 成功下载，退出方法
                    }
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载的文件大小为0");
                } else {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载图片失败，HTTP状态码：" + response.getStatus());
                }
            } catch (Exception e) {
                lastException = e;
                currentTry++;
                if (currentTry < maxRetries) {
                    // 等待一段时间后重试
                    Thread.sleep(1000 * currentTry);
                    continue;
                }
                log.error("下载图片失败，URL: {}, 重试次数: {}, 错误: {}", url, currentTry, e.getMessage());
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载图片失败: " + e.getMessage());
            }
        }
        
        if (lastException != null) {
            throw lastException;
        }
    }

    @Override
    protected String getOriginalFilename(Object inputSource) {
        String url = (String) inputSource;
        return FileUtil.getName(url);
    }

    @Override
    protected void validPicture(Object inputSource) {
        String url = (String) inputSource;
        //1.校验非空
        ThrowUtils.throwIf(StrUtil.isBlank(url), ErrorCode.PARAMS_ERROR, "URL不能为空");
        
        //2.校验URL格式和协议
        try {
            URL urlObj = new URL(url);
            String protocol = urlObj.getProtocol();
            ThrowUtils.throwIf(!protocol.equals("http") && !protocol.equals("https"), 
                ErrorCode.PARAMS_ERROR, "URL协议错误");
        } catch (MalformedURLException e) {
            ThrowUtils.throwIf(true, ErrorCode.PARAMS_ERROR, "URL格式错误");
        }

        //3.发送head请求，添加请求头
        HttpResponse response = null;
        try {
            response = HttpUtil.createRequest(Method.HEAD, url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .header("Accept", "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8")
                .header("Referer", "https://www.bing.com/")
                .timeout(5000)  // 设置5秒超时
                .execute();
                
            // 放宽状态码检查，某些服务器可能不支持HEAD请求
            if(response.getStatus() != HttpStatus.HTTP_OK && response.getStatus() != HttpStatus.HTTP_NOT_FOUND) {
                log.warn("URL响应异常状态码: {}, URL: {}", response.getStatus(), url);
            }
            
            //文件类型校验（可选）
            String contentType = response.header("Content-Type");
            if (StrUtil.isNotBlank(contentType) && !contentType.toLowerCase().contains("image")) {
                log.warn("可能不是图片类型: {}, URL: {}", contentType, url);
            }
            
            //文件大小校验（可选）
            String contentLength = response.header("Content-Length");
            if (StrUtil.isNotBlank(contentLength)) {
                try {
                    long fileSize = Long.parseLong(contentLength);
                    final long TEN_M = 10 * 1024 * 1024;
                    if (fileSize > TEN_M) {
                        log.warn("文件可能过大: {} bytes, URL: {}", fileSize, url);
                    }
                } catch (NumberFormatException e) {
                    log.warn("无法获取文件大小，URL: {}", url);
                }
            }
        } catch (Exception e) {
            log.warn("校验图片URL失败: {}, URL: {}", e.getMessage(), url);
            // 不抛出异常，让下载阶段再试一次
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
