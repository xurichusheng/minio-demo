package com.wjh.resp;

import org.apache.http.HttpStatus;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 接口响应参数
 * 
 * @author wenjianhai
 * @date 2022/1/17
 * @since JDK 1.8
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
public class ResponseData {

    private int code;
    private String msg;
    private Object data;

    public ResponseData() {}

    public ResponseData(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static ResponseData success() {
        return new ResponseData(HttpStatus.SC_OK, "操作成功", null);
    }

    public static ResponseData success(String msg) {
        return new ResponseData(HttpStatus.SC_OK, msg, null);
    }

    public static ResponseData success(Object data) {
        return new ResponseData(HttpStatus.SC_OK, "操作成功", data);
    }

    public static ResponseData success(String msg, Object data) {
        return new ResponseData(HttpStatus.SC_OK, msg, data);
    }

    public static ResponseData error() {
        return new ResponseData(HttpStatus.SC_INTERNAL_SERVER_ERROR, "操作失败", null);
    }

    public static ResponseData error(String msg) {
        return new ResponseData(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg, null);
    }

    public static ResponseData error(String msg, Object data) {
        return new ResponseData(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg, data);
    }

    public static ResponseData error(int code, String msg) {
        return new ResponseData(code, msg, null);
    }

    public static ResponseData error(int code, String msg, Object data) {
        return new ResponseData(code, msg, data);
    }
}
