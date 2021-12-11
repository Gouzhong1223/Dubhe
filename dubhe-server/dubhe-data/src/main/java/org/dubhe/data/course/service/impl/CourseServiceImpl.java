package org.dubhe.data.course.service.impl;

import org.dubhe.biz.base.constant.ResponseCode;
import org.dubhe.biz.base.context.UserContext;
import org.dubhe.biz.base.service.UserContextService;
import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.biz.dataresponse.factory.DataResponseFactory;
import org.dubhe.biz.log.enums.LogEnum;
import org.dubhe.biz.log.utils.LogUtil;
import org.dubhe.data.course.dao.*;
import org.dubhe.data.course.domain.Course;
import org.dubhe.data.course.domain.CourseFile;
import org.dubhe.data.course.domain.CourseSchedule;
import org.dubhe.data.course.domain.CourseType;
import org.dubhe.data.course.domain.dto.CourseCreateDTO;
import org.dubhe.data.course.domain.dto.CourseDetailDTO;
import org.dubhe.data.course.domain.dto.CourseTypeDetailDTO;
import org.dubhe.data.course.domain.dto.CourseUpdateDTO;
import org.dubhe.data.course.service.CourseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private static final Integer DEFAULT_FINISH_CHAPTER = 0;
    private static final Integer DEFAULT_SCHEDULE = 0;
    private static final Integer DEFAULT_DONE = 0;
    private static final Integer DEFAULT_COURSE_STATUS = 1;

    private final CourseFileMapper courseFileMapper;
    private final CourseMapper courseMapper;
    private final UserContextService userContextService;
    private final CourseScheduleMapper courseScheduleMapper;
    private final CourseTypeMapper courseTypeMapper;
    private final CourseChapterScheduleMapper courseChapterScheduleMapper;
    private final CourseChapterMapper courseChapterMapper;

    public CourseServiceImpl(CourseFileMapper courseFileMapper,
                             CourseMapper courseMapper,
                             UserContextService userContextService,
                             CourseScheduleMapper courseScheduleMapper,
                             CourseTypeMapper courseTypeMapper,
                             CourseChapterScheduleMapper courseChapterScheduleMapper,
                             CourseChapterMapper courseChapterMapper) {
        this.courseFileMapper = courseFileMapper;
        this.courseMapper = courseMapper;
        this.userContextService = userContextService;
        this.courseScheduleMapper = courseScheduleMapper;
        this.courseTypeMapper = courseTypeMapper;
        this.courseChapterScheduleMapper = courseChapterScheduleMapper;
        this.courseChapterMapper = courseChapterMapper;
    }

    @Override
    public DataResponseBody listAllCourses() {
        // 获取当前用户信息
        UserContext curUser = userContextService.getCurUser();
        Long userId = curUser.getId();

        // 获取所有的课程分类
        List<CourseType> courseTypes = courseTypeMapper.selectAll();

        // 根据分类查询所有的课程并且组装 courseDTO
        ArrayList<CourseTypeDetailDTO> courseTypeDetailDTOS = new ArrayList<>();

        courseTypes.forEach(e -> {
            // 构建 courseTypeDetailDTO
            CourseTypeDetailDTO courseTypeDetailDTO = new CourseTypeDetailDTO();
            courseTypeDetailDTO.setCourseTypeId(e.getId());
            courseTypeDetailDTO.setCourseTypeName(e.getName());
            // 构建 CourseDetailDTO 列表
            ArrayList<CourseDetailDTO> courseDetailDTOS = new ArrayList<>();

            // 根据课程类型 ID 查询 Course
            List<Course> courses;
            if (curUser.getNickName().equals("admin")) {
                // 管理员能看到所有课程
                courses = courseMapper.selectAllByType(e.getId());
            } else {
                // 用户只能看到已经激活的课程
                courses = courseMapper.selectAllByTypeAndStatus(e.getId(), DEFAULT_COURSE_STATUS);
            }
            courses.forEach(course -> {
                CourseDetailDTO courseDetailDTO = generateCourseDetailDTO(userId, course);
                // 添加结果集
                courseDetailDTOS.add(courseDetailDTO);
            });

            // 设置 courseTypeDetailDTO 的 Course 为 courseDetailDTOS
            courseTypeDetailDTO.setCourses(courseDetailDTOS);

            // 将组装的 courseTypeDetailDTO 添加到结果集
            courseTypeDetailDTOS.add(courseTypeDetailDTO);
        });
        return new DataResponseBody<>(courseTypeDetailDTOS);
    }

    /**
     * 根据 userId 和 courseID 生成 CourseDetailDTO
     *
     * @param userId userId
     * @param course course
     * @return CourseDetailDTO
     */
    private CourseDetailDTO generateCourseDetailDTO(Long userId, Course course) {
        // 根据 userId 和 courseId 查询学习进度
        CourseSchedule courseSchedule = courseScheduleMapper.selectOneByUserIdAndCourseId(userId, course.getId());
        CourseDetailDTO courseDetailDTO = new CourseDetailDTO();
        // 设置课程 ID
        courseDetailDTO.setCourseId(course.getId());
        // 设置课程名字
        courseDetailDTO.setCourseName(course.getName());
        // 设置课程简介
        courseDetailDTO.setIntroduction(course.getIntroduction());
        // 设置总章节
        courseDetailDTO.setTotalChapter(course.getTotalChapters());
        // 判断用户是否有学习记录
        if (courseSchedule != null) {
            // 有学习记录则从 courseSchedule 中取
            courseDetailDTO.setFinishChapter(courseSchedule.getLearnedChapterNum());
            courseDetailDTO.setSchedule(courseSchedule.getSchedule());
            courseDetailDTO.setStartTime(courseSchedule.getStartTime());
            courseDetailDTO.setLastStudyTime(courseSchedule.getLastUpdateTime());
            courseDetailDTO.setDone(courseSchedule.getDone());
        } else {
            // 如果没有学习记录则将进度相关变量设置为默认值
            courseDetailDTO.setFinishChapter(DEFAULT_FINISH_CHAPTER);
            courseDetailDTO.setSchedule(DEFAULT_SCHEDULE);
            courseDetailDTO.setStartTime(null);
            courseDetailDTO.setLastStudyTime(null);
            courseDetailDTO.setDone(DEFAULT_DONE);
        }
        return courseDetailDTO;
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
        if (courseRecord == null) {
            return new DataResponseBody(ResponseCode.ERROR, "封面图 ID 不存在!");
        }
        try {
            courseMapper.updateByPrimaryKeySelective(courseRecord);
            LogUtil.info(LogEnum.COURSE, "更新课程:" + courseRecord);
        } catch (Exception e) {
            LogUtil.error(LogEnum.COURSE, "更新课程失败:" + e.getMessage());
            e.printStackTrace();
        }
        return new DataResponseBody<>(courseRecord);
    }

    @Override
    public DataResponseBody deleteCourse(Long courseId) {
        // 判断课程是否存在
        Course course = courseMapper.selectByPrimaryKey(courseId);
        if (course == null) {
            return DataResponseFactory.failed("课程不存在!");
        }
        // 获取当前用户的 ID
        Long userId = userContextService.getCurUserId();
        // 删除课程
        courseMapper.deleteByPrimaryKey(courseId);
        // 删除课程里面所有的章节
        courseChapterMapper.deleteByCourseId(courseId);
        // 删除课程章节的学习记录
        courseChapterScheduleMapper.deleteByCourseIdAndUserId(courseId, userId);
        // 删除课程的学习进度
        courseScheduleMapper.deleteByCourseIdAndUserId(courseId, userId);
        return DataResponseFactory.success();
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
        if (courseFile == null) {
            return null;
        }
        courseRecord.setName(courseUpdateDTO.getCourseName());
        courseRecord.setType(courseUpdateDTO.getCourseTypeId());
        courseRecord.setIntroduction(courseUpdateDTO.getIntroduction());
        courseRecord.setCoverImage(courseFile.getUrl());
        courseRecord.setStatus(courseUpdateDTO.getStatus());
        courseRecord.setUpdateTime(LocalDateTime.now());
        return courseRecord;
    }
}
