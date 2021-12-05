package org.dubhe.course.service.impl;

import org.dubhe.biz.base.constant.ResponseCode;
import org.dubhe.biz.base.service.UserContextService;
import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.biz.log.enums.LogEnum;
import org.dubhe.biz.log.utils.LogUtil;
import org.dubhe.course.dao.CourseFileMapper;
import org.dubhe.course.dao.CourseMapper;
import org.dubhe.course.domain.Course;
import org.dubhe.course.domain.CourseFile;
import org.dubhe.course.domain.dto.CourseCreateDTO;
import org.dubhe.course.domain.dto.CourseUpdateDTO;
import org.dubhe.course.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description : 课程业务实现
 * @Date : create by QingSong in 2021-11-22 14:53
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.service.impl
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@Service
@Transactional
public class CourseServiceImpl implements CourseService {

    private static final Integer DEFAULT_TOTAL_CHAPTER = 0;
    private static final Integer DEFAULT_COURSE_STATUS = 1;

    private final CourseFileMapper courseFileMapper;
    private final CourseMapper courseMapper;
    private final UserContextService userContextService;

    public CourseServiceImpl(CourseFileMapper courseFileMapper, CourseMapper courseMapper, UserContextService userContextService) {
        this.courseFileMapper = courseFileMapper;
        this.courseMapper = courseMapper;
        this.userContextService = userContextService;
    }

    @Override
    public List<Course> listAllCourses() {
        return null;
    }

    @Override
    public DataResponseBody createCourse(CourseCreateDTO courseCreateDTO) {
        CourseFile courseFile = courseFileMapper.selectByPrimaryKey(courseCreateDTO.getCoverImageId());
        if (courseFile == null) {
            return new DataResponseBody(ResponseCode.ERROR, "封面图 ID 不存在!");
        }
        Course courseRecord = new Course(null, courseCreateDTO.getCourseName(), courseCreateDTO.getCourseTypeId(), courseCreateDTO.getIntroduction(), DEFAULT_TOTAL_CHAPTER, LocalDateTime.now(), LocalDateTime.now(), courseFile.getUrl(), DEFAULT_COURSE_STATUS, userContextService.getCurUserId());
        courseMapper.insertSelective(courseRecord);
        LogUtil.info(LogEnum.COURSE, "新增课程:" + courseRecord);
        return new DataResponseBody<>(courseRecord);
    }

    @Override
    public DataResponseBody updateCourse(CourseUpdateDTO courseUpdateDTO) {
        Course courseRecord = updateCourseFromDTO(courseUpdateDTO);
        try {
            courseMapper.updateByPrimaryKeySelective(courseRecord);
            LogUtil.info(LogEnum.COURSE, "更新课程:" + courseRecord);
        } catch (Exception e) {
            LogUtil.error(LogEnum.COURSE, "更新课程失败:" + e.getMessage());
            e.printStackTrace();
        }
        return new DataResponseBody<>(courseRecord);
    }

    /**
     * 根据 DTO 转换 Course
     *
     * @param courseUpdateDTO courseUpdateDTO
     * @return Course
     */
    private Course updateCourseFromDTO(CourseUpdateDTO courseUpdateDTO) {
        Course courseRecord = courseMapper.selectByPrimaryKey(courseUpdateDTO.getCourseId());
        CourseFile courseFile = courseFileMapper.selectByPrimaryKey(courseUpdateDTO.getCoverImageId());
        courseRecord.setName(courseUpdateDTO.getCourseName());
        courseRecord.setType(courseUpdateDTO.getCourseTypeId());
        courseRecord.setIntroduction(courseUpdateDTO.getIntroduction());
        courseRecord.setCoverImage(courseFile.getUrl());
        courseRecord.setStatus(courseUpdateDTO.getStatus());
        courseRecord.setUpdateTime(LocalDateTime.now());
        return courseRecord;
    }
}
