package com.wjh.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 配置参数类
 * 
 * @author wenjianhai
 * @date 2022/1/17
 * @since JDK 1.8
 */
@Slf4j
@Data
@Configuration
public class MinioConfig {
    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.accessKey}")
    private String accessKey;
    @Value("${minio.secretKey}")
    private String secretKey;
    @Value("${minio.bucketName}")
    private String bucketName;
    /** 文件失效时间（天） */
    @Value("${minio.file_expiry_days}")
    private int fileExpiryDays;

    @Bean
    public MinioClient minioClient() {
        log.info("初始化 MinioClient 开始");
        try {
            return MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build();
        } catch (Exception e) {
            log.error("初始化 MinioClient 失败:" + e.getMessage(), e);
            return null;
        }
    }
}
