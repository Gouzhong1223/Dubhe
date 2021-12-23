package org.dubhe.data.course.domain.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dubhe.data.course.domain.Course;
import org.dubhe.data.course.domain.CourseType;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-23 21:11
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.data.course.domain.dto
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ACourseTypeDetailDTO {
    /**
     * 课程分类
     */
    private CourseType courseType;

    /**
     * 课程
     */
    private List<ACourseDetailDTO> aCourseDetailDTOS;
}
