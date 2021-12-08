package org.dubhe.course.service;

import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.course.domain.dto.CourseChapterCreateDTO;
import org.dubhe.course.domain.dto.CourseChapterUpdateDTO;

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

    /**
     * 学习课程章节
     *
     * @param chapterId 章节 ID
     * @param courseId  课程 ID
     * @return DataResponseBody
     */
    DataResponseBody studyCourseChapter(Long chapterId, Long courseId);

    /**
     * 上传课程章节
     *
     * @param courseChapterCreateDTO courseChapterCreateDTO
     * @return DataResponseBody
     */
    DataResponseBody createCourseChapter(CourseChapterCreateDTO courseChapterCreateDTO);

    /**
     * 更新章节信息
     *
     * @param courseChapterUpdateDTO courseChapterUpdateDTO
     * @return DataResponseBody
     */
    DataResponseBody updateCourseChapter(CourseChapterUpdateDTO courseChapterUpdateDTO);

    /**
     * 删除章节信息
     *
     * @param courseChapterId 章节 ID
     * @return DataResponseBody
     */
    DataResponseBody deleteCourseChapter(Long courseChapterId);
}
