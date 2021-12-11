package org.dubhe.data.course.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-05 21:53
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.domain.dto
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CourseDetailDTO {
    /**
     * 课程 ID
     */
    private Long courseId;
    /**
     * 课程名字
     */
    private String courseName;
    /**
     * 课程简介
     */
    private String introduction;
    /**
     * 课程总章节
     */
    private Integer totalChapter;
    /**
     * 已经完成的章节
     */
    private Integer finishChapter;
    /**
     * 进度
     */
    private Integer schedule;
    /**
     * 开始学习时间
     */
    private LocalDateTime startTime;
    /**
     * 上次学习时间
     */
    private LocalDateTime lastStudyTime;

    /**
     * 是否完成 0-未完成 1-完成
     */
    private Integer done;
}
