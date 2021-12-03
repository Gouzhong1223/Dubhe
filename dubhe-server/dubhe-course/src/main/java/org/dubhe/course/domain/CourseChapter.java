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
@ApiModel(value = "dubhe-cloud-prod.course_chapter")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseChapter implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 章节主键
     */
    @ApiModelProperty(value = "章节主键")
    private Long id;
    /**
     * 章节名字
     */
    @ApiModelProperty(value = "章节名字")
    private String name;
    /**
     * 章节序号
     */
    @ApiModelProperty(value = "章节序号")
    private Integer serialNumber;
    /**
     * 章节类型 0-pdf 1-视频 2-PPT
     */
    @ApiModelProperty(value = "章节类型 0-pdf 1-视频 2-PPT")
    private Integer chapterType;
    /**
     * 章节所属课程
     */
    @ApiModelProperty(value = "章节所属课程")
    private Long courseId;
    /**
     * 章节简介
     */
    @ApiModelProperty(value = "章节简介")
    private String introduction;
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
     * 文件 ID
     */
    @ApiModelProperty(value = "文件 ID")
    private Long fileId;
}
