package org.dubhe.course.service;

import org.dubhe.biz.base.vo.DataResponseBody;

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
}
