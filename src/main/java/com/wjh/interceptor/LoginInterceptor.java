package com.wjh.interceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.wjh.conf.RedisClient;
import com.wjh.exception.LoginException;
import com.wjh.util.Constants;
import com.wjh.util.YmlParams;

import lombok.extern.slf4j.Slf4j;

/**
 * 登录拦截器
 * 
 * @author wenjianhai
 * @date 2022/1/19
 * @since JDK 1.8
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

	@Resource
	private RedisClient redisClient;

	/**
	 * 在请求处理之前进行调用（Controller方法调用之前）
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String token = request.getHeader(Constants.TOKEN);

		if (StringUtils.isBlank(token)) {
			log.error("登录拦截，token为空.");
			throw new LoginException("请求头中的token不能为空");
		}
		// 当前登录人
		Object loginObj = redisClient.get(YmlParams.getLoginKey() + token);

		if (loginObj == null) {
			log.error("登录拦截，用户尚未登录.");
			throw new LoginException("请重新登录");
		}
		return true;
	}

	/**
	 * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}

	/**
	 * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
	}
}
