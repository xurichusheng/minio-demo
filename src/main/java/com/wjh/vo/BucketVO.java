package com.wjh.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Minio存储桶
 * 
 * @author wenjianhai
 * @date 2022/1/18
 * @since JDK 1.8
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class BucketVO implements Serializable {

	private static final long serialVersionUID = -7078176219040233386L;

	/** 桶名 */
	private String name;
	/** 创建时间 */
	private String createTime;
}
