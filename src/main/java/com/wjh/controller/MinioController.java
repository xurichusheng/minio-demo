package com.wjh.controller;

import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wjh.resp.ResponseData;
import com.wjh.util.MinIoUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author wenjianhai
 * @date 2022/1/17
 * @since JDK 1.8
 */
@Slf4j
@RestController
@RequestMapping("/minio")
public class MinioController {

	@Resource
	private MinIoUtil minIoUtil;

	@Resource
	private HttpServletRequest httpRequest;

	/**
	 * 获取所有的存储桶
	 *
	 * @date 2022-01-17
	 * @return ResponseData
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@ResponseBody
	@GetMapping("/all/buckets")
	public ResponseData getAllBuckets() {
		return ResponseData.success(minIoUtil.getAllBuckets(httpRequest));
	}

	/**
	 * 上传文件
	 *
	 * @date 2022-01-18
	 * @param file       文件
	 * @param bucketName 存储桶名
	 * @return ResponseData
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@ResponseBody
	@PostMapping("/upload")
	public ResponseData upload(@RequestParam("file") MultipartFile file,
			@RequestParam("bucketName") String bucketName) {
		// 文件流
		InputStream is = null;

		try {
			// 文件流
			is = file.getInputStream();
			// 文件名
			String fileName = file.getOriginalFilename();

			// 10000以内的随机数（防止多线程下文件名重复）
			int random = ThreadLocalRandom.current().nextInt(10000);
			// 文件名前缀
			String prefix = System.currentTimeMillis() + "" + random;

			String newFileName = prefix + "." + StringUtils.substringAfterLast(fileName, ".");
			// 类型
			String contentType = file.getContentType();

			minIoUtil.upload(bucketName, is, newFileName, contentType, httpRequest);

			log.info("上传文件-成功.文件名:{}, 新文件名:{}, 存储桶名:{}", fileName, newFileName, bucketName);

			return ResponseData.success("操作成功", newFileName);
		} catch (Exception e) {
			log.error("上传文件-失败:" + e.getMessage(), e);
			return ResponseData.error();
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * 下载文件
	 *
	 * @date 2022-01-18
	 * @param bucketName 存储桶名
	 * @param fileName   文件名
	 * @return ResponseData
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@ResponseBody
	@GetMapping("/download")
	public ResponseData download(@RequestParam("bucketName") String bucketName,
			@RequestParam("fileName") String fileName, HttpServletResponse response) {

		InputStream is = null;

		try {
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			response.setContentType("application/force-download");
			response.setCharacterEncoding("UTF-8");

			is = minIoUtil.download(bucketName, fileName, httpRequest);

			IOUtils.copy(is, response.getOutputStream());

			return ResponseData.success();
		} catch (Exception e) {
			log.error("下载文件失败-失败:" + e.getMessage(), e);
			return ResponseData.error();
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * 获取文件地址
	 * <p>
	 * 临时地址
	 * </p>
	 * 
	 * @date 2022-01-18
	 * @param bucketName 存储桶名
	 * @param fileName   文件名
	 * @return ResponseData
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@ResponseBody
	@GetMapping("/file/url")
	public ResponseData getFileUrl(@RequestParam("bucketName") String bucketName,
			@RequestParam("fileName") String fileName) {
		return ResponseData.success("操作成功", minIoUtil.getFileUrl(bucketName, fileName, httpRequest));
	}

	/**
	 * 获取存储桶中的所有文件
	 * 
	 * @date 2022-01-19
	 * @param bucketName 存储桶名
	 * @return ResponseData
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@ResponseBody
	@GetMapping("/file/list/{bucketName}")
	public ResponseData fileList(@PathVariable("bucketName") String bucketName) {
		return ResponseData.success(minIoUtil.fileList(bucketName, httpRequest));
	}
}
