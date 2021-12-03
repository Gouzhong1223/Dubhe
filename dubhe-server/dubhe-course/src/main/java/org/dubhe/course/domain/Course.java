package org.dubhe.course.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-03 22:33
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.domain
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@ApiModel(value = "dubhe-cloud-prod.course")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 课程主键
     */
    @ApiModelProperty(value = "课程主键")
    private Long id;
    /**
     * 课程名字
     */
    @ApiModelProperty(value = "课程名字")
    private String name;
    /**
     * 课程类型
     */
    @ApiModelProperty(value = "课程类型")
    private Long type;
    /**
     * 课程简介
     */
    @ApiModelProperty(value = "课程简介")
    private String introduction;
    /**
     * 总共章节
     */
    @ApiModelProperty(value = "总共章节")
    private Integer totalChapters;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
    /**
     * 封面图
     */
    @ApiModelProperty(value = "封面图")
    private String coverImage;
    /**
     * 状态码 0-不可见 1-可见
     */
    @ApiModelProperty(value = "状态码 0-不可见 1-可见")
    private Integer status;
    /**
     * 创建者 ID
     */
    @ApiModelProperty(value = "创建者 ID")
    private Integer createUserId;
}
