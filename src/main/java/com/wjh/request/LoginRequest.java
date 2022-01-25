package com.wjh.request;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 登录请求参数
 * 
 * @author wenjianhai
 * @date 2022/1/19
 * @since JDK 1.8
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class LoginRequest implements Serializable {

    private static final long serialVersionUID = 8719760703180257423L;

    /** 登录名 */
    @NotBlank(message = "登录名不能为空")
    private String loginName;
    /** 密码 */
    @NotBlank(message = "密码不能为空")
    private String password;
}
