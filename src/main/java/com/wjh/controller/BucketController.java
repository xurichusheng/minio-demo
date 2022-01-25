package com.wjh.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.wjh.request.GetFileUrlRequest;
import com.wjh.resp.ResponseData;
import com.wjh.util.Constants;
import com.wjh.util.MinIoUtil;
import com.wjh.util.YmlParams;
import com.wjh.vo.FileItemVO;

import lombok.extern.slf4j.Slf4j;

/**
 * Minio存储桶接口
 * 
 * @author wenjianhai
 * @date 2022/1/19
 * @since JDK 1.8
 */
@Slf4j
@Controller
@RequestMapping("/bucket/")
public class BucketController {

	@Resource
	private MinIoUtil minIoUtil;

	/**
	 * 获取所有的存储桶
	 *
	 * @date 2022-01-19
	 * @param model
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@ResponseBody
	@RequestMapping(value = "list", method = RequestMethod.GET)
	public ResponseData bucketList(Model model, HttpServletRequest httpRequest) {
		return ResponseData.success(minIoUtil.getAllBuckets(httpRequest));
	}

	/**
	 * 跳转到文件列表页面
	 *
	 * @date 2022-01-19
	 * @param bucketName 存储桶名
	 * @param model
	 * @return
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@RequestMapping(value = "filePage", method = RequestMethod.GET)
	public String toFileList(@RequestParam String bucketName, Model model, HttpServletRequest httpRequest) {
		log.info("跳转到文件列表页面-开始，存储桶名:{}", bucketName);
//		List<FileItemVO> list = minIoUtil.fileList(bucketName, httpRequest);
//		list = null == list ? new ArrayList<>() : list;

		model.addAttribute(Constants.TOKEN, httpRequest.getHeader(Constants.TOKEN));
//		model.addAttribute("list", list);

		return "fileList";
	}

	/**
	 * 查询指定存储桶中的所有文件
	 *
	 * @date 2022-01-20
	 * @param bucketName  存储桶名
	 * @param httpRequest
	 * @return ResponseData
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@ResponseBody
	@RequestMapping(value = "file/list/{bucketName}", method = RequestMethod.GET)
	public ResponseData fileList(@PathVariable String bucketName, HttpServletRequest httpRequest) {
		log.info("查询指定存储桶中的所有文件-开始，存储桶名:{}", bucketName);
		// 获取存储桶中的所有文件
		List<FileItemVO> list = minIoUtil.fileList(bucketName, httpRequest);

		if (CollectionUtils.isNotEmpty(list)) {
			// 域名
			String domain = YmlParams.getMinioEndpoint();

			if (!domain.endsWith("/")) {
				domain += "/";
			}
			for (FileItemVO vo : list) {
				// 文件永久链接
				vo.setForeverUrl(domain + bucketName + "/" + vo.getName());
			}
		}
		list = null == list ? new ArrayList<>() : list;

		return ResponseData.success(list);
	}

	/**
	 * 获取文件地址
	 * 
	 * @date 2022-01-20
	 * @param request 请求参数
	 * @return ResponseData
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@ResponseBody
	@PostMapping("/file/url")
	public ResponseData getFileUrl(@RequestBody @Validated GetFileUrlRequest request, HttpServletRequest httpRequest) {
		log.info("获取文件地址-开始，请求参数:{}", JSON.toJSONString(request));
		String url = minIoUtil.getFileUrl(request.getBucketName(), request.getFileName(), httpRequest);
		return ResponseData.success("操作成功", url);
	}

	@RequestMapping(value = "/file/upload", method = RequestMethod.POST)
	public void upload(HttpServletRequest request, @RequestParam MultipartFile file,
			@RequestParam String bucketName) {
		log.info("文件上传-开始，存储桶名:{}", bucketName);

		if (file == null || StringUtils.isBlank(file.getOriginalFilename()) || StringUtils.isBlank(bucketName)) {
			log.error("文件上传，数据为空");
			return;
		}
		// 文件名
		String fileName = file.getOriginalFilename();
		InputStream is = null;

		try {
			is = file.getInputStream();
			String contentType = file.getContentType();

			minIoUtil.upload(bucketName, is, fileName, contentType, request);
		} catch (Exception e) {
			log.error(String.format("文件上传-失败，存储桶名:%s，文件名:%s", bucketName, fileName), e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * 下载文件
	 * <p>
	 * 文件永久链接下载
	 * </p>
	 * 
	 * @date 2022-01-20
	 * @return ResponseData
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@RequestMapping(value = "/file/download", method = RequestMethod.GET)
	public void download(HttpServletRequest request, HttpServletResponse response) {
		log.info("下载文件-开始");
		// 网络链接（文件的http链接）
		String filenamerel = request.getParameter("filenamerel");
		// 文件名
		String fileName = request.getParameter("filename");

		if (StringUtils.isBlank(fileName)) {
			fileName = this.getFileNameFormUrl(filenamerel);
		}
		InputStream is = null;
		OutputStream os = null;

		try {
			// 从网络读取文件
			URL url = new URL(filenamerel);
			URLConnection conn = url.openConnection();

			is = conn.getInputStream();
			os = response.getOutputStream();

			String userAgent = request.getHeader("user-agent").toLowerCase();

			if (userAgent.contains("msie") || userAgent.contains("like gecko")) {
				// win10 ie edge 浏览器 和其他系统的ie
				fileName = URLEncoder.encode(fileName, "UTF-8");
			} else {
				// 文件名转码
				fileName = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
			}
			response.setContentType("application/octet-stream; charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

			// 文件缓存区
			byte[] bytes = new byte[1024];
			// 判断输入流中的数据是否已经读完的标致
			int len = 0;
			while ((len = is.read(bytes)) > 0) {
				os.write(bytes, 0, len);
			}
			log.info("下载文件-结束.文件:{}", filenamerel);
		} catch (Exception e) {
			log.error(String.format("下载文件-失败!文件:%s", filenamerel), e);
		} finally {
			IOUtils.closeQuietly(os, is);
		}
	}

	/**
	 * 获取网络链接（http）中的文件名
	 *
	 * @date 2022-01-20
	 * @param filenamerel 网络链接（http）
	 * @return String
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	private String getFileNameFormUrl(String filenamerel) {
		if (StringUtils.isBlank(filenamerel)) {
			return "";
		}
		int lastIdx = filenamerel.lastIndexOf("/");

		String fileName = "";

		if (lastIdx >= 0) {
			fileName = filenamerel.substring(lastIdx + 1, filenamerel.length());
		}
		return fileName;
	}
}
