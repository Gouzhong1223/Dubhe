package org.dubhe.course.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dubhe.course.domain.CourseUserSchedule;

import java.util.List;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-03 22:34
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.mapper
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@Mapper
public interface CourseUserScheduleMapper {
    int insert(CourseUserSchedule record);

    int insertSelective(CourseUserSchedule record);

    List<CourseUserSchedule> selectAllByUserId(@Param("userId") Long userId);

    /**
     * 查询所有的学习记录
     *
     * @return 所有学习记录
     */
    List<CourseUserSchedule> selectAll();
}
