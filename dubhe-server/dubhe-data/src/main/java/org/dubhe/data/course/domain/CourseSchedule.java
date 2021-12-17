package org.dubhe.data.course.domain;

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
 * @Date : create by QingSong in 2021-12-17 21:38
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.data.course.domain
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@ApiModel(value = "course_schedule")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseSchedule implements Serializable {
    /**
     * 学习进度 ID
     */
    @ApiModelProperty(value = "学习进度 ID")
    private Long id;

    /**
     * 开始学习时间
     */
    @ApiModelProperty(value = "开始学习时间")
    private LocalDateTime startTime;

    /**
     * 最后更新时间
     */
    @ApiModelProperty(value = "最后更新时间")
    private LocalDateTime lastUpdateTime;

    /**
     * 需要学习的章节数
     */
    @ApiModelProperty(value = "需要学习的章节数")
    private Integer totalChapterNum;

    /**
     * 已经学习的章节数
     */
    @ApiModelProperty(value = "已经学习的章节数")
    private Integer learnedChapterNum;

    /**
     * 学习进度 如 85 表示 85%
     */
    @ApiModelProperty(value = "学习进度 如 85 表示 85%")
    private Integer schedule;

    /**
     * 是否已经完成,已完成-1  未完成-0
     */
    @ApiModelProperty(value = "是否已经完成,已完成-1  未完成-0")
    private Integer done;

    /**
     * 所属课程 ID
     */
    @ApiModelProperty(value = "所属课程 ID")
    private Long courseId;

    /**
     * userID
     */
    @ApiModelProperty(value = "userID")
    private Long userId;

    private static final long serialVersionUID = 1L;
}
