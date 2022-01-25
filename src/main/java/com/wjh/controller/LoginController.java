package com.wjh.controller;

import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.wjh.conf.RedisClient;
import com.wjh.request.LoginRequest;
import com.wjh.util.Constants;
import com.wjh.util.YmlParams;
import com.wjh.vo.LoginUserVO;

import lombok.extern.slf4j.Slf4j;

/**
 * 登录接口
 * 
 * @author wenjianhai
 * @date 2022/1/19
 * @since JDK 1.8
 */
@Slf4j
@Controller
@RequestMapping("/login/")
public class LoginController {

	@Resource
	private RedisClient redisClient;

	@RequestMapping(value = "tolist", method = RequestMethod.POST)
	public String login(LoginRequest request, Model model, HttpServletRequest httpRequest) {

		log.info("登录-开始，请求参数:{}", JSON.toJSONString(request));

		if (StringUtils.isBlank(request.getLoginName())) {
			return "";
		}
		if (StringUtils.isBlank(request.getPassword())) {
			return "";
		}
		LoginUserVO user = LoginUserVO.builder().loginName(request.getLoginName()).password(request.getPassword())
				.build();

		String token = UUID.randomUUID().toString().replaceAll("-", "");

		redisClient.set(YmlParams.getLoginKey() + token, user, YmlParams.getLoginTimeout());

		log.info("登录-成功，登录名:{}, token:{}", request.getLoginName(), token);

		model.addAttribute(Constants.TOKEN, token);

		return "list";
	}
}
