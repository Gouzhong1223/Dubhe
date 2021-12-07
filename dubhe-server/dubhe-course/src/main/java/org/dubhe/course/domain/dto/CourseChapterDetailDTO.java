package org.dubhe.course.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description : CourseChapterDetailDTO
 * @Date : create by QingSong in 2021-12-06 20:52
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
public class CourseChapterDetailDTO {
    /**
     * 章节 ID
     */
    private Long chapterId;
    /**
     * 章节名字
     */
    private String chapterName;
    /**
     * 章节序号
     */
    private Integer serialNumber;
    /**
     * 章节简介
     */
    private String introduction;
    /**
     * 该用户是否已经学习 0-为学习 1-已学习
     */
    private Integer learned;
    /**
     * 章节类型 章节类型 0-pdf 1-视频 2-PPT
     */
    private Integer chapterType;
    /**
     * 所属课程 ID
     */
    private Long courseId;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 文件 URL
     */
    private String fileUrl;
}
