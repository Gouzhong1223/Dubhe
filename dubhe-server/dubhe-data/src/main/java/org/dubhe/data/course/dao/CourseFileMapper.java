package org.dubhe.data.course.dao;

import org.dubhe.data.course.domain.CourseFile;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-17 21:38
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.data.course.dao
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
public interface CourseFileMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseFile record);

    int insertSelective(CourseFile record);

    CourseFile selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseFile record);

    int updateByPrimaryKey(CourseFile record);
}