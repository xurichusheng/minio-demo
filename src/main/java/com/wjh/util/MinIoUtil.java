package com.wjh.util;

import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.wjh.conf.MinioConfig;
import com.wjh.conf.RedisClient;
import com.wjh.vo.BucketVO;
import com.wjh.vo.FileItemVO;
import com.wjh.vo.LoginUserVO;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListObjectsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveBucketArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * minio服务工具类
 * 
 * @author wenjianhai
 * @date 2022/1/17
 * @since JDK 1.8
 */
@Slf4j
@Component
public class MinIoUtil {

	@Resource
	private MinioConfig minioConfig;

//	@Resource
//	private MinioClient minioClient;

	@Resource
	private RedisClient redisClient;

	/**
	 * 获取所有的存储桶
	 *
	 * @date 2022-01-17
	 * @return List
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@SneakyThrows(Exception.class)
	public List<BucketVO> getAllBuckets(HttpServletRequest httpRequest) {
		MinioClient minioClient = this.minioClient(httpRequest);

		if (minioClient == null) {
			log.error("获取所有的存储桶,MinioClient为空");
			return null;
		}
		// 查询所有的存储桶
		List<Bucket> list = minioClient.listBuckets();

		if (CollectionUtils.isNotEmpty(list)) {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
					.withZone(ZoneId.of("Asia/Shanghai"));

			return list.stream().map(o -> {
				ZonedDateTime creationDate = o.creationDate();

				BucketVO vo = new BucketVO();
				vo.setName(o.name());
				vo.setCreateTime(creationDate.format(formatter));

				return vo;
			}).collect(Collectors.toList());
		}
		return null;
	}

	/**
	 * 判断存储桶是否存在
	 * 
	 * @date 2022-01-17
	 * @param bucketName 桶名
	 * @return boolean
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@SneakyThrows(Exception.class)
	public boolean bucketExists(String bucketName, HttpServletRequest httpRequest) {
		MinioClient minioClient = this.minioClient(httpRequest);

		if (minioClient == null) {
			log.error("判断存储桶是否存在,MinioClient为空");
			return false;
		}
		BucketExistsArgs args = BucketExistsArgs.builder().bucket(bucketName).build();
		return minioClient.bucketExists(args);
	}

	/**
	 * 创建存储桶
	 *
	 * @date 2022-01-17
	 * @param bucketName 桶名
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@SneakyThrows(Exception.class)
	public void createBucket(String bucketName, HttpServletRequest httpRequest) {
		if (!bucketExists(bucketName, httpRequest)) {
			MinioClient minioClient = this.minioClient(httpRequest);

			if (minioClient == null) {
				log.error("创建存储桶,MinioClient为空");
				return;
			}
			MakeBucketArgs args = MakeBucketArgs.builder().bucket(bucketName).build();
			minioClient.makeBucket(args);
		}
	}

	/**
	 * 删除存储桶
	 *
	 * @date 2022-01-17
	 * @param bucketName 桶名
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@SneakyThrows(Exception.class)
	public void deleteBucket(String bucketName, HttpServletRequest httpRequest) {
		MinioClient minioClient = this.minioClient(httpRequest);

		if (minioClient == null) {
			log.error("删除存储桶,MinioClient为空");
			return;
		}
		RemoveBucketArgs args = RemoveBucketArgs.builder().bucket(bucketName).build();
		minioClient.removeBucket(args);
	}

	/**
	 * 文件上传
	 *
	 * @date 2022-01-17
	 * @param bucketName  桶名
	 * @param is          文件流
	 * @param fileName    文件名
	 * @param contentType 文件路径
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@SneakyThrows(Exception.class)
	public void upload(String bucketName, InputStream is, String fileName, String contentType,
			HttpServletRequest httpRequest) {
		MinioClient minioClient = this.minioClient(httpRequest);

		if (minioClient == null) {
			log.error("文件上传,MinioClient为空");
			return;
		}
		PutObjectArgs args = PutObjectArgs.builder().bucket(bucketName).object(fileName).contentType(contentType)
				.stream(is, is.available(), -1).build();
		minioClient.putObject(args);
	}

	/**
	 * 获取文件地址
	 *
	 * @date 2022-01-17
	 * @param bucketName 桶名
	 * @param fileName   文件名
	 * @return String
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@SneakyThrows(Exception.class)
	public String getFileUrl(String bucketName, String fileName, HttpServletRequest httpRequest) {
		MinioClient minioClient = this.minioClient(httpRequest);

		if (minioClient == null) {
			log.error("获取文件地址,MinioClient为空");
			return "";
		}
//		Map<String, String> reqParams = new HashMap<String, String>();
//		reqParams.put("response-content-type", "application/json");

		GetPresignedObjectUrlArgs args = GetPresignedObjectUrlArgs.builder().method(Method.GET).bucket(bucketName)
				.object(fileName).expiry(minioConfig.getFileExpiryDays(), TimeUnit.DAYS) // 文件失效时间（天）
				/* .extraQueryParams(reqParams) */.build();
		return minioClient.getPresignedObjectUrl(args);
	}

	/**
	 * 下载文件
	 *
	 * @date 2022-01-17
	 * @param bucketName 桶名
	 * @param fileName   文件名
	 * @return InputStream
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@SneakyThrows(Exception.class)
	public InputStream download(String bucketName, String fileName, HttpServletRequest httpRequest) {
		MinioClient minioClient = this.minioClient(httpRequest);

		if (minioClient == null) {
			log.error("下载文件,MinioClient为空");
			return null;
		}
		GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(bucketName).object(fileName).build();
		return minioClient.getObject(getObjectArgs);
	}

	@SneakyThrows(Exception.class)
	public List<FileItemVO> fileList(String bucketName, HttpServletRequest httpRequest) {
		MinioClient minioClient = this.minioClient(httpRequest);

		if (minioClient == null) {
			log.error("获取存储桶中的所有文件,MinioClient为空");
			return null;
		}
		ListObjectsArgs args = ListObjectsArgs.builder().bucket(bucketName).build();

		Iterable<Result<Item>> it = minioClient.listObjects(args);

		if (it != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
					.withZone(ZoneId.of("Asia/Shanghai"));

			List<FileItemVO> list = new ArrayList<>();
			FileItemVO vo = null;

			for (Result<Item> result : it) {
				Item item = result.get();

				if (item != null) {
					vo = FileItemVO.builder().name(item.objectName())
							.ownerName(item.owner() == null ? "" : item.owner().displayName()).size(item.size())
							.isDir(item.isDir()).encodingType("").build();

					if (item.lastModified() != null) {
						vo.setLastModifyTime(item.lastModified().format(formatter));
					}
					list.add(vo);
				}
			}
			return list;
		}
		return null;
	}

	/**
	 * 删除文件
	 *
	 * @date 2022-01-17
	 * @param bucketName 桶名
	 * @param fileName   文件名
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@SneakyThrows(Exception.class)
	public void deleteFile(String bucketName, String fileName, HttpServletRequest httpRequest) {
		MinioClient minioClient = this.minioClient(httpRequest);

		if (minioClient == null) {
			log.error("删除文件,MinioClient为空");
			return;
		}
		RemoveObjectArgs args = RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build();
		minioClient.removeObject(args);
	}

	private MinioClient minioClient(HttpServletRequest httpRequest) {
		String token = httpRequest.getHeader(Constants.TOKEN);
		// 当前登录人
		Object loginObj = redisClient.get(YmlParams.getLoginKey() + token);

		if (loginObj != null && loginObj instanceof LoginUserVO) {
			// 当前登录人
			LoginUserVO user = (LoginUserVO) loginObj;

			return MinioClient.builder().endpoint(YmlParams.getMinioEndpoint())
					.credentials(user.getLoginName(), user.getPassword()).build();
		}
		log.error("获取MinioClient，当前登录人为空");
		return null;
	}
}
