package org.dubhe.course.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-03 22:34
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.domain
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@ApiModel(value = "dubhe-cloud-prod.course_user_schedule")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseUserSchedule implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * userId
     */
    @ApiModelProperty(value = "userId")
    private Long userId;
    /**
     * 进度 ID
     */
    @ApiModelProperty(value = "进度 ID")
    private Long scheduleId;
}
