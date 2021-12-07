package org.dubhe.course.dao;

import org.apache.ibatis.annotations.Param;
import org.dubhe.course.domain.CourseChapterSchedule;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-06 20:59
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.dao
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
public interface CourseChapterScheduleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseChapterSchedule record);

    int insertSelective(CourseChapterSchedule record);

    CourseChapterSchedule selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseChapterSchedule record);

    int updateByPrimaryKey(CourseChapterSchedule record);

    /**
     * 根据课程ID,userId,章节 ID,查询章节学习进度
     *
     * @param userId    userId
     * @param chapterId chapterId
     * @param courseId  courseId
     * @return CourseChapterSchedule
     */
    CourseChapterSchedule selectOneByUserIdAndChapterIdAndCourseId(@Param("userId") Long userId, @Param("chapterId") Long chapterId, @Param("courseId") Long courseId);
}
