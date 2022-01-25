package com.wjh.vo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 文件信息
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
public class FileItemVO implements Serializable {

    private static final long serialVersionUID = -1142710454267009679L;

    /** 文件名 */
    private String name;
    /** 最后更新时间 */
    private String lastModifyTime;
    /** 作者 */
    private String ownerName;
    /** 文件大小（字节） */
    private long size;
    /** 是否目录 */
    private boolean isDir;
    /** 文件类型 */
    private String encodingType;
    /** 文件永久链接 */
    private String foreverUrl;
}
