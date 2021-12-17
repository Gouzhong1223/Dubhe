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
 * @Date : create by QingSong in 2021-12-17 21:35
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.data.course.domain
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@ApiModel(value = "course_type")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseType implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 课程类型主键
     */
    @ApiModelProperty(value = "课程类型主键")
    private Long id;
    /**
     * 课程类型名字
     */
    @ApiModelProperty(value = "课程类型名字")
    private String name;
    /**
     * 课程创建时间
     */
    @ApiModelProperty(value = "课程创建时间")
    private LocalDateTime createTime;
    /**
     * 课程更新时间
     */
    @ApiModelProperty(value = "课程更新时间")
    private LocalDateTime updateTime;
}
