package org.dubhe.course.mapper;

import org.dubhe.course.domain.CourseUserSchedule;

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
public interface CourseUserScheduleMapper {
    int insert(CourseUserSchedule record);

    int insertSelective(CourseUserSchedule record);
}
