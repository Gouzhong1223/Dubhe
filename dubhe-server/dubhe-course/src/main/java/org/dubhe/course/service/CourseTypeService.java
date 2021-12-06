package org.dubhe.course.service;

import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.course.domain.dto.CourseTypeUpdateDTO;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-04 19:27
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.service
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
public interface CourseTypeService {
    /**
     * 创建课程分类
     *
     * @param courseTypeName 课程分类名字
     * @return DataResponseBody
     */
    DataResponseBody createCourseType(String courseTypeName);

    /**
     * 更新课程分类信息
     *
     * @param courseTypeUpdateDTO courseTypeUpdateDTO
     * @return DataResponseBody
     */
    DataResponseBody updateCourseType(CourseTypeUpdateDTO courseTypeUpdateDTO);

    /**
     * 删除课程分类
     *
     * @param courseTypeId 课程分类 ID
     * @return DataResponseBody
     */
    DataResponseBody deleteCourseType(Long courseTypeId);
}
