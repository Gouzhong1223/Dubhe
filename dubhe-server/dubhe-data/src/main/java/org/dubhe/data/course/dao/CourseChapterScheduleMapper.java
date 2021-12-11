package org.dubhe.data.course.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dubhe.data.course.domain.CourseChapterSchedule;

import java.util.List;

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
@Mapper
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

    /**
     * 根据用户 ID 和课程 ID 删除章节学习记录
     *
     * @param courseId 课程 ID
     * @param userId   用户 ID
     * @return int
     */
    int deleteByCourseIdAndUserId(@Param("courseId") Long courseId, @Param("userId") Long userId);

    /**
     * 根据章节 ID 删除学习记录
     *
     * @param chapterId 章节 ID
     * @return 删除数
     */
    int deleteByChapterId(@Param("chapterId") Long chapterId);

    /**
     * 用户所有学习过这一章节的用户 ID
     *
     * @param chapterId 章节 ID
     * @return 所有学习过这一章节的用户 ID
     */
    List<Long> selectUserIdByChapterId(@Param("chapterId") Long chapterId);
}