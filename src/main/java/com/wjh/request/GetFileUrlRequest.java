package com.wjh.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 获取文件地址请求参数
 * 
 * @author wenjianhai
 * @date 2022/1/20
 * @since JDK 1.8
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class GetFileUrlRequest implements Serializable {

    private static final long serialVersionUID = 6188402308220120801L;

    /** 存储桶名 */
    @NotBlank(message = "存储桶名不能为空")
    private String bucketName;
    /** 文件名 */
    @NotBlank(message = "文件名能为空")
    private String fileName;
}
