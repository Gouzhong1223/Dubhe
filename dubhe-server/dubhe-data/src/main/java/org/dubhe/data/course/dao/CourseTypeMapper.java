package org.dubhe.data.course.dao;

import org.apache.ibatis.annotations.Param;
import org.dubhe.data.course.domain.CourseType;

import java.util.List;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-17 21:35
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.data.course.dao
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
public interface CourseTypeMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseType record);

    int insertSelective(CourseType record);

    CourseType selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseType record);

    int updateByPrimaryKey(CourseType record);

    CourseType selectOneByName(@Param("name") String name);

    List<CourseType> selectAll();
}
