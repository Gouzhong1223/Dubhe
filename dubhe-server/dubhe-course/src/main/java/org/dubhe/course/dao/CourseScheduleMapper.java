package org.dubhe.course.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dubhe.course.domain.CourseSchedule;

import java.util.List;

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
@Mapper
public interface CourseScheduleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseSchedule record);

    int insertSelective(CourseSchedule record);

    CourseSchedule selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseSchedule record);

    int updateByPrimaryKey(CourseSchedule record);

    CourseSchedule selectOneByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    List<CourseSchedule> selectAllByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据用户 ID 和课程 ID 删除课程学习进度
     *
     * @param courseId 课程 ID
     * @param userId   用户 ID
     * @return 删除数量
     */
    int deleteByCourseIdAndUserId(@Param("courseId") Long courseId, @Param("userId") Long userId);

    /**
     * 查询所有的学习记录
     *
     * @return List<CourseSchedule>
     */
    List<CourseSchedule> selectAll();
}
