package com.wjh;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.wjh.util.YmlParams;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wenjianhai
 * @date 2021年7月16日
 * @since JDK 11
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestMinio {

	@Test
	public void minioClient() {
		try {
			MinioClient client = MinioClient.builder().endpoint(YmlParams.getMinioEndpoint())
					.credentials("minioadmin", "minioadmin").build();
			System.out.println("MinioClient=" + client);
		} catch (Exception e) {
			log.error("初始化 MinioClient 失败:" + e.getMessage(), e);
		}
	}
}
