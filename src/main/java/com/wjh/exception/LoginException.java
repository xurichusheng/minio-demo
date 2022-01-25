package com.wjh.exception;

/**
 * 登录异常
 *
 * @author wenjianhai
 * @date 2022/1/19
 * @since JDK 1.8
 */
public class LoginException extends Exception {

    private static final long serialVersionUID = -3045026407297745315L;

    public LoginException() {
        super();
    }

    public LoginException(String message) {
        super(message);
    }

    public LoginException(Throwable cause) {
        super(cause);
    }

    public LoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
