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
 * @Date : create by QingSong in 2021-12-06 20:59
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.domain
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@ApiModel(value = "dubhe-cloud-prod.course_chapter_schedule")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseChapterSchedule implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 章节进度主键
     */
    @ApiModelProperty(value = "章节进度主键")
    private Long id;
    /**
     * 学习时间
     */
    @ApiModelProperty(value = "学习时间")
    private LocalDateTime startTime;
    /**
     * 所属课程 ID
     */
    @ApiModelProperty(value = "所属课程 ID")
    private Long courseId;
    /**
     * 所属章节 ID
     */
    @ApiModelProperty(value = "所属章节 ID")
    private Long chapterId;
    /**
     * userId
     */
    @ApiModelProperty(value = "userId")
    private Long userId;
}
