package org.dubhe.data.course.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dubhe.data.course.domain.Course;

import java.util.ArrayList;


/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-05 22:01
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
public class CourseTypeDetailDTO4Admin {
    /**
     * 课程分类 ID
     */
    private Long courseTypeId;
    /**
     * 课程分类名字
     */
    private String courseTypeName;
    /**
     * 课程 DTO
     */
    private ArrayList<Course> courses;
}
