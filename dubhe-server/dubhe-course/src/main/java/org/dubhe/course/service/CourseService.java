package org.dubhe.course.service;

import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.course.domain.dto.CourseCreateDTO;
import org.dubhe.course.domain.dto.CourseUpdateDTO;

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
     * @return List<Course>
     */
    DataResponseBody listAllCourses();

    /**
     * 创建课程
     *
     * @param courseCreateDTO courseCreateDTO
     * @return DataResponseBody
     */
    DataResponseBody createCourse(CourseCreateDTO courseCreateDTO);

    /**
     * 更新课程信息
     *
     * @param courseUpdateDTO courseUpdateDTO
     * @return DataResponseBody
     */
    DataResponseBody updateCourse(CourseUpdateDTO courseUpdateDTO);

    /**
     * 删除课程信息
     *
     * @param courseId 课程 ID
     * @return DataResponseBody
     */
    DataResponseBody deleteCourse(Long courseId);
}
