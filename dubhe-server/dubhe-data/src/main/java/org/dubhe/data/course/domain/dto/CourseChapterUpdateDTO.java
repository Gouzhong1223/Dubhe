package org.dubhe.data.course.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-07 20:55
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.domain.dto
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseChapterUpdateDTO {

    /**
     * 章节主键
     */
    @NotNull(message = "courseChapterId 不能为空")
    private Long courseChapterId;

    /**
     * 课程 ID
     */
    @NotNull(message = "课程 ID 不能为空")
    private Long courseId;
    /**
     * 章节名字
     */
    @NotNull(message = "courseChapterName 不能为空")
    private String courseChapterName;
    /**
     * 章节序号
     */
    @NotNull(message = "serialNumber 不能为空")
    private Integer serialNumber;
    /**
     * 章节类型
     */
    @NotNull(message = "chapterType 不能为空")
    private Integer chapterType;
    /**
     * 章节简介
     */
    @NotNull(message = "introduction 不能为空")
    private String introduction;
    /**
     * 文件 ID
     */
    @NotNull(message = "fileId 不能为空")
    private Long fileId;
}
