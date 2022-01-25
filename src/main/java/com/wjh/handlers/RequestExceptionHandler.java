package com.wjh.handlers;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.wjh.exception.LoginException;
import com.wjh.resp.ResponseData;

import io.minio.errors.ErrorResponseException;
import lombok.extern.slf4j.Slf4j;

/**
 * 请求参数异常拦截
 * 
 * @author wenjianhai
 * @date 2022/1/19
 * @since JDK 1.8
 */
@Slf4j
@RestControllerAdvice
public class RequestExceptionHandler {

	/**
	 * 参数合法性校验异常
	 *
	 * @param exception
	 * @return
	 */
	@ResponseBody
	@ExceptionHandler(BindException.class)
	public ResponseData validationBodyException(BindException exception) {

		return this.getExceptionMsg(exception.getBindingResult());
	}

	/**
	 * 参数合法性校验异常
	 *
	 * @param exception
	 * @return
	 */
	@ResponseBody
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseData validationBodyException(MethodArgumentNotValidException exception) {

		return this.getExceptionMsg(exception.getBindingResult());
	}

	/**
	 * 登录异常处理
	 *
	 * @date 2022-01-19
	 * @param e
	 * @return ResponseData
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@ResponseBody
	@ExceptionHandler(LoginException.class)
	public ResponseData loginException(LoginException e) {
		return ResponseData.builder().code(HttpStatus.SC_BAD_REQUEST).msg(e.getMessage()).build();
	}

	/**
	 * minio操作异常
	 * 
	 * @date 2022-01-19
	 * @param e
	 * @return ResponseData
	 * @since JDK 1.8
	 * @author wenjianhai
	 */
	@ResponseBody
	@ExceptionHandler(ErrorResponseException.class)
	public ResponseData minioException(ErrorResponseException e) {
		log.error(String.format("minio操作异常:%s", e.getMessage()), e);

		if (StringUtils.isNotBlank(e.getMessage())
				&& e.getMessage().contains("The Access Key Id you provided does not exist in our records")) {
			return ResponseData.builder().code(HttpStatus.SC_UNAUTHORIZED).msg("用户名或密码不正确").build();
		}
		return ResponseData.builder().code(HttpStatus.SC_BAD_REQUEST).msg("操作失败").build();
	}

	private ResponseData getExceptionMsg(BindingResult result) {

		StringBuffer buffer = new StringBuffer();

		if (result != null && result.hasErrors()) {

			List<ObjectError> errors = result.getAllErrors();

			errors.forEach(p -> {

				FieldError fieldError = (FieldError) p;
				log.error("Data check failure : object{" + fieldError.getObjectName() + "},field{"
						+ fieldError.getField() + "},errorMessage{" + fieldError.getDefaultMessage() + "}");
				buffer.append(fieldError.getDefaultMessage()).append(",");
			});
		}
		String msg = StringUtils.isBlank(buffer.toString()) ? "参数不合法"
				: buffer.toString().substring(0, buffer.toString().length() - 1);

		return ResponseData.builder().code(HttpStatus.SC_BAD_REQUEST).msg(msg).build();
	}
}
