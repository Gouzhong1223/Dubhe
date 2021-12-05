package org.dubhe.course.dao;

import org.apache.ibatis.annotations.Param;
import org.dubhe.course.domain.CourseSchedule;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-05 22:36
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.dao
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
public interface CourseScheduleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseSchedule record);

    int insertSelective(CourseSchedule record);

    CourseSchedule selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseSchedule record);

    int updateByPrimaryKey(CourseSchedule record);

    CourseSchedule selectOneByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);
}
