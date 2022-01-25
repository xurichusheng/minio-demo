package com.wjh.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录用户信息
 * @author wenjianhai
 * @date 2022/1/19
 * @since JDK 1.8
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class LoginUserVO implements Serializable {

    private static final long serialVersionUID = -3427848903655118947L;

    /** 用户Id */
    private String id;
    /** 登录名 */
    private String loginName;
    /** 用户名 */
    private String userName;
    /** 密码 */
    private String password;
}
