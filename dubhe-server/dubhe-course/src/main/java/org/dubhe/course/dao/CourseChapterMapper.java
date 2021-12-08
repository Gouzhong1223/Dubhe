package org.dubhe.course.dao;

import org.apache.ibatis.annotations.Param;
import org.dubhe.course.domain.CourseChapter;

import java.util.List;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-03 22:33
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.mapper
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
public interface CourseChapterMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CourseChapter record);

    int insertSelective(CourseChapter record);

    CourseChapter selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(CourseChapter record);

    int updateByPrimaryKey(CourseChapter record);

    /**
     * 根据课程 ID 查询所有的章节
     *
     * @param courseId 课程ID
     * @return List<CourseChapter>
     */
    List<CourseChapter> selectAllByCourseId(@Param("courseId") Long courseId);

    /**
     * 根据课程 ID 删除所有的章节
     *
     * @param courseId 课程 ID
     * @return int
     */
    int deleteByCourseId(@Param("courseId") Long courseId);
}
