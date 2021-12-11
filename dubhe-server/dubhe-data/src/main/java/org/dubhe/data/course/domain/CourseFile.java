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
 * @Date : create by QingSong in 2021-12-04 22:11
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.domain
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@ApiModel(value = "dubhe-cloud-prod.course_file")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseFile implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 课程文件 ID
     */
    @ApiModelProperty(value = "课程文件 ID")
    private Long id;
    /**
     * 课程文件名字
     */
    @ApiModelProperty(value = "课程文件名字")
    private String name;
    /**
     * 课程文件类型
     */
    @ApiModelProperty(value = "课程文件类型")
    private String type;
    /**
     * 课程文件 URL
     */
    @ApiModelProperty(value = "课程文件 URL")
    private String url;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private LocalDateTime updateTime;
    /**
     * 课程文件 uri
     */
    @ApiModelProperty(value = "课程文件 uri")
    private String uri;
}
