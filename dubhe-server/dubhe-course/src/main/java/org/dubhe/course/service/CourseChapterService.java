package org.dubhe.course.service;

import org.dubhe.biz.base.vo.DataResponseBody;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-06 21:04
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.service
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
public interface CourseChapterService {
    /**
     * 根据课程 ID 获取所有的章节
     *
     * @param courseId 课程 ID
     * @return DataResponseBody
     */
    DataResponseBody listAllCourseChapter(Long courseId);
}
