package org.dubhe.data.course.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-05 21:10
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.domain.dto
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@AllArgsConstructor
@Data
@NotNull
@NotNull
public class CourseUpdateDTO {
    @NotNull(message = "课程 ID 不能为空")
    private Long courseId;
    @NotNull(message = "课程名字不能为空")
    private String courseName;
    @NotNull(message = "课程类型主键不能为空")
    private Long courseTypeId;
    @NotNull(message = "课程简介不能为空")
    private String introduction;
    @NotNull(message = "课程封面图 ID 不能为空")
    private Long coverImageId;
    @NotNull(message = "课程状态码不能为空")
    private Integer status;
}
