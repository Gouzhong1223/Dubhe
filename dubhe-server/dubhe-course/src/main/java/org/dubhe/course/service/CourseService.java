package org.dubhe.course.service;

import org.dubhe.course.domain.Course;
import org.dubhe.course.domain.dto.CourseCreateDTO;

import java.util.List;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description : 课程业务接口
 * @Date : create by QingSong in 2021-11-22 14:53
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.service
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
public interface CourseService {
    /**
     * 获取所有的课程
     *
     * @return
     */
    List<Course> listAllCourses();

    /**
     * 创建课程
     *
     * @param courseCreateDTO courseCreateDTO
     * @return Course
     */
    Course createCourse(CourseCreateDTO courseCreateDTO);
}
