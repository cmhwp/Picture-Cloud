package com.photo.pcloudbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.photo.pcloudbackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class PCloudBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PCloudBackendApplication.class, args);
    }

}
