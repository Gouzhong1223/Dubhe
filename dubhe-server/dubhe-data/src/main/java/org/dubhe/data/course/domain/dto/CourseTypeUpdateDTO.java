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
 * @Date : create by QingSong in 2021-12-06 16:34
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.domain.dto
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CourseTypeUpdateDTO {
    @NotNull(message = "课程分类 ID 不能为空")
    private Long courseTypeId;
    @NotNull(message = "课程分类名字不能为空")
    private String courseTypeName;
}
