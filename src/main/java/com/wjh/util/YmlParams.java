package com.wjh.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * yml文件配置项
 * 
 * @author wenjianhai
 * @date 2022/1/19
 * @since JDK 1.8
 */
@Configuration
@Component
@ConfigurationProperties
public class YmlParams {
    /** 应用服务端口号 */
    private static int serverPort;
    /** minio服务地址 */
    private static String minioEndpoint;
    /** minio服务用户名 */
    private static String minioAccessKey;
    /** minio服务密码 */
    private static String minioSecretKey;
    /** minio服务存储桶名 */
    private static String minioBucketName;
    /** minio服务文件失效时间（天） */
    private static int minioFileExpiryDays;

    /** 登录缓存key */
    private static String loginKey;
    /** MinioClient缓存key */
    private static String minioClientKey;

    /** 登录超时时间（秒） */
    private static long loginTimeout;

    public static int getServerPort() {
        return serverPort;
    }
    @Value("${server.port}")
    public void setServerPort(int serverPort) {
        YmlParams.serverPort = serverPort;
    }

    public static String getMinioEndpoint() {
        return minioEndpoint;
    }
    @Value("${minio.endpoint}")
    public void setMinioEndpoint(String minioEndpoint) {
        YmlParams.minioEndpoint = minioEndpoint;
    }

    public static String getMinioAccessKey() {
        return minioAccessKey;
    }
    @Value("${minio.accessKey}")
    public void setMinioAccessKey(String minioAccessKey) {
        YmlParams.minioAccessKey = minioAccessKey;
    }

    public static String getMinioSecretKey() {
        return minioSecretKey;
    }
    @Value("${minio.secretKey}")
    public void setMinioSecretKey(String minioSecretKey) {
        YmlParams.minioSecretKey = minioSecretKey;
    }

    public static String getMinioBucketName() {
        return minioBucketName;
    }
    @Value("${minio.bucketName}")
    public void setMinioBucketName(String minioBucketName) {
        YmlParams.minioBucketName = minioBucketName;
    }

    public static int getMinioFileExpiryDays() {
        return minioFileExpiryDays;
    }
    @Value("${minio.file_expiry_days}")
    public void setMinioFileExpiryDays(int minioFileExpiryDays) {
        YmlParams.minioFileExpiryDays = minioFileExpiryDays;
    }

    public static String getLoginKey() {
        return loginKey;
    }
    @Value("${spring.redis.key.login_key}")
    public void setLoginKey(String loginKey) {
        YmlParams.loginKey = loginKey;
    }

    public static String getMinioClientKey() {
        return minioClientKey;
    }
    @Value("${spring.redis.key.minio_client_key}")
    public void setMinioClientKey(String minioClientKey) {
        YmlParams.minioClientKey = minioClientKey;
    }

    public static long getLoginTimeout() {
        return loginTimeout;
    }
    @Value("${login.timeout}")
    public void setLoginTimeout(long loginTimeout) {
        YmlParams.loginTimeout = loginTimeout;
    }
}
