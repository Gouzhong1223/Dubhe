package org.dubhe.course.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-11-24 22:09
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.domain
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseCreateDTO {
    @NotNull(message = "课程名称不能为空")
    private String courseName;
    @NotNull(message = "课程类型主键不能为空")
    private Long courseTypeId;
    @NotNull(message = "课程简介不能为空")
    private String introduction;
    @NotNull(message = "课程封面图 ID 不能为空")
    private Long coverImageId;
}
