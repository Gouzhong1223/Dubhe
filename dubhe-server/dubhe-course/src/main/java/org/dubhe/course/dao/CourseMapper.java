package org.dubhe.course.dao;

import org.apache.ibatis.annotations.Param;
import org.dubhe.course.domain.Course;

import java.util.List;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-05 12:42
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.dao
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
public interface CourseMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Course record);

    int insertSelective(Course record);

    Course selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Course record);

    int updateByPrimaryKey(Course record);

    List<Course> selectAllByType(@Param("type") Long type);

    /**
     * 根据状态和类型查询课程
     *
     * @param type   类型
     * @param status 状态
     * @return List<Course>
     */
    List<Course> selectAllByTypeAndStatus(@Param("type") Long type, @Param("status") Integer status);

    /**
     * 根据分类查询一个 Course
     *
     * @param type 分类 ID
     * @return Course
     */
    Course selectOneByType(@Param("type") Long type);
}
