package org.dubhe.data.course.service.impl;

import org.dubhe.biz.base.context.UserContext;
import org.dubhe.biz.base.service.UserContextService;
import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.biz.dataresponse.factory.DataResponseFactory;
import org.dubhe.biz.permission.base.BaseService;
import org.dubhe.data.course.dao.*;
import org.dubhe.data.course.domain.*;
import org.dubhe.data.course.domain.dto.CourseChapterCreateDTO;
import org.dubhe.data.course.domain.dto.CourseChapterDetailDTO;
import org.dubhe.data.course.domain.dto.CourseChapterUpdateDTO;
import org.dubhe.data.course.domain.dto.admin.ACourseDetailDTO;
import org.dubhe.data.course.domain.dto.admin.ACourseTypeDetailDTO;
import org.dubhe.data.course.service.CourseChapterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-06 21:04
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.service.impl
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@Service
@Transactional
public class CourseChapterServiceImpl implements CourseChapterService {

    private final CourseChapterScheduleMapper courseChapterScheduleMapper;
    private final UserContextService userContextService;
    private final CourseChapterMapper courseChapterMapper;
    private final CourseFileMapper courseFileMapper;
    private final CourseScheduleMapper courseScheduleMapper;
    private final CourseMapper courseMapper;
    private final CourseUserScheduleMapper courseUserScheduleMapper;
    private final CourseTypeMapper courseTypeMapper;

    public CourseChapterServiceImpl(CourseChapterScheduleMapper courseChapterScheduleMapper,
                                    UserContextService userContextService,
                                    CourseChapterMapper courseChapterMapper,
                                    CourseFileMapper courseFileMapper,
                                    CourseScheduleMapper courseScheduleMapper,
                                    CourseMapper courseMapper,
                                    CourseUserScheduleMapper courseUserScheduleMapper,
                                    CourseTypeMapper courseTypeMapper) {
        this.courseChapterScheduleMapper = courseChapterScheduleMapper;
        this.userContextService = userContextService;
        this.courseChapterMapper = courseChapterMapper;
        this.courseFileMapper = courseFileMapper;
        this.courseScheduleMapper = courseScheduleMapper;
        this.courseMapper = courseMapper;
        this.courseUserScheduleMapper = courseUserScheduleMapper;
        this.courseTypeMapper = courseTypeMapper;
    }

    @Override
    public DataResponseBody listAllCourseChapter(Long courseId) {
        // 获取当前用户 ID
        Long userId = userContextService.getCurUserId();
        // 根据当前课程 ID 查询所有章节
        List<CourseChapter> courseChapters = courseChapterMapper.selectAllByCourseId(courseId);
        // 构建返回结果集
        ArrayList<CourseChapterDetailDTO> courseChapterDetailDTOS = new ArrayList<>();
        courseChapters.forEach(e -> {
            // courseChapterDetailDTO 模型转换
            CourseChapterDetailDTO courseChapterDetailDTO = getCourseChapterDetailDTO(courseId, userId, e);
            // 将 courseChapterDetailDTO 添加到结果集
            courseChapterDetailDTOS.add(courseChapterDetailDTO);
        });
        // 按照章节序号排序
        courseChapterDetailDTOS.sort(Comparator.comparingInt(CourseChapterDetailDTO::getSerialNumber));
        return DataResponseFactory.success(courseChapterDetailDTOS);
    }

    @Override
    public DataResponseBody studyCourseChapter(Long chapterId, Long courseId) {
        Course course = courseMapper.selectByPrimaryKey(courseId);
        if (course == null) {
            return DataResponseFactory.failed("课程不存在!");
        }
        CourseChapter courseChapter = courseChapterMapper.selectByPrimaryKey(chapterId);
        if (courseChapter == null) {
            return DataResponseFactory.failed("章节不存在!");
        }
        Long userId = userContextService.getCurUserId();
        // 查询是否有该章节的学习记录
        CourseChapterSchedule chapterSchedule = courseChapterScheduleMapper.selectOneByUserIdAndChapterIdAndCourseId(userId, chapterId, courseId);
        if (chapterSchedule != null) {
            // 已经学过该章节了
            // 直接查询课程章节并返回 更新课程最后一次学习时间
            CourseSchedule courseSchedule = courseScheduleMapper.selectOneByUserIdAndCourseId(userId, courseId);
            courseSchedule.setLastUpdateTime(LocalDateTime.now());
            courseScheduleMapper.updateByPrimaryKeySelective(courseSchedule);
            // 返回章节信息
            return getCourseChapterDataResponseBody(chapterId);
        } else {
            // 第一次学习该章节
            CourseChapterSchedule courseChapterSchedule = new CourseChapterSchedule(null, LocalDateTime.now(), courseId, chapterId, userId);
            // 插入章节学习记录
            courseChapterScheduleMapper.insertSelective(courseChapterSchedule);
            // 更新课程预览看到的学习记录
            CourseSchedule courseSchedule = courseScheduleMapper.selectOneByUserIdAndCourseId(userId, courseId);
            // 更新课程学习进度
            updateOrNotCourseSchedule(courseId, userId, course, courseSchedule);
        }
        // 查询课程章节
        return getCourseChapterDataResponseBody(chapterId);
    }

    /**
     * 获取章节详情并封装结果集
     *
     * @param chapterId 章节 ID
     * @return DataResponseBody
     */
    private DataResponseBody getCourseChapterDataResponseBody(Long chapterId) {
        CourseChapter courseChapter = courseChapterMapper.selectByPrimaryKey(chapterId);
        HashMap<String, Object> resultMap = new HashMap<>(2);
        resultMap.put("courseChapter", courseChapter);
        resultMap.put("fileUrl", courseFileMapper.selectByPrimaryKey(courseChapter.getFileId()).getUrl());
        return DataResponseFactory.success(resultMap);
    }

    @Override
    public DataResponseBody createCourseChapter(CourseChapterCreateDTO courseChapterCreateDTO) {
        Course course = courseMapper.selectByPrimaryKey(courseChapterCreateDTO.getCourseId());
        if (course == null) {
            return DataResponseFactory.failed("课程不存在!");
        }
        CourseFile courseFile = courseFileMapper.selectByPrimaryKey(courseChapterCreateDTO.getFileId());
        if (courseFile == null) {
            return DataResponseFactory.failed("文件不存在!");
        }
        CourseChapter courseChapter = getCourseChapter(courseChapterCreateDTO);
        courseChapterMapper.insertSelective(courseChapter);
        updateCourseSchedules(course);
        return DataResponseFactory.success(courseChapter);
    }

    @Override
    public DataResponseBody updateCourseChapter(CourseChapterUpdateDTO courseChapterUpdateDTO) {
        CourseFile courseFile = courseFileMapper.selectByPrimaryKey(courseChapterUpdateDTO.getFileId());
        if (courseFile == null) {
            return DataResponseFactory.failed("文件不存在");
        }
        Course course = courseMapper.selectByPrimaryKey(courseChapterUpdateDTO.getCourseId());
        if (course == null) {
            return DataResponseFactory.failed("课程不存在");
        }
        CourseChapter courseChapter = courseChapterMapper.selectByPrimaryKey(courseChapterUpdateDTO.getCourseChapterId());
        if (courseChapter == null) {
            return DataResponseFactory.failed("章节 ID 不存在");
        }
        extracted(courseChapterUpdateDTO, courseChapter);
        courseChapterMapper.updateByPrimaryKeySelective(courseChapter);
        return DataResponseFactory.success(courseChapter);
    }

    @Override
    public DataResponseBody deleteCourseChapter(Long courseChapterId) {
        // 判断章节是否存在
        CourseChapter courseChapter = courseChapterMapper.selectByPrimaryKey(courseChapterId);
        if (courseChapter == null) {
            return DataResponseFactory.failed("章节不存在");
        }
        // 删除章节
        courseChapterMapper.deleteByPrimaryKey(courseChapterId);
        // 更新课程学习进度
        // 查询所有学习过这一章节的用户 ID
        List<Long> userIds = courseChapterScheduleMapper.selectUserIdByChapterId(courseChapterId);
        // 查询所有学习记录
        List<CourseSchedule> courseSchedules = courseScheduleMapper.selectAll();
        courseSchedules.forEach(courseSchedule -> {
            if (userIds.contains(courseSchedule.getUserId())) {
                // 已经学习过此章节的用户所产生的学习进度需要修改,将已学习的章节-1
                courseSchedule.setLearnedChapterNum(courseSchedule.getLearnedChapterNum() - 1);
            }
            // 将课程的总章节-1
            courseSchedule.setTotalChapterNum(courseSchedule.getTotalChapterNum() - 1);
            if (Objects.equals(courseSchedule.getLearnedChapterNum(), courseSchedule.getTotalChapterNum())) {
                // 如果总章节-1 之后用户的学习章节和总章节一样就将学习进度完结
                courseSchedule.setSchedule(100);
                courseSchedule.setDone(1);
            }
            courseScheduleMapper.updateByPrimaryKeySelective(courseSchedule);
        });

        // 删除章节学习记录
        courseChapterScheduleMapper.deleteByChapterId(courseChapterId);

        // 更新课程
        Course course = courseMapper.selectByPrimaryKey(courseChapter.getCourseId());
        course.setTotalChapters(course.getTotalChapters() - 1);
        course.setUpdateTime(LocalDateTime.now());

        // 返回
        return DataResponseFactory.success();
    }

    @Override
    public DataResponseBody listAllCourseChapterA() {
        // 获取当前用户
        UserContext curUser = userContextService.getCurUser();
        // 判断是否管理员
        if (!BaseService.isAdmin(curUser)) {
            return DataResponseFactory.failed("非管理员不能调用此接口!");
        }
        // 获取所有课程分类
        List<CourseType> courseTypes = courseTypeMapper.selectAll();
        // 构造结果集
        ArrayList<ACourseTypeDetailDTO> resultList = new ArrayList<>();
        courseTypes.forEach(courseType -> {
            ACourseTypeDetailDTO aCourseTypeDetailDTO = new ACourseTypeDetailDTO();
            aCourseTypeDetailDTO.setCourseType(courseType);
            // 根据课程分类查询所有的课程
            List<Course> courses = courseMapper.selectAllByType(courseType.getId());
            ArrayList<ACourseDetailDTO> aCourseDetailDTOS = new ArrayList<>();
            courses.forEach(course -> {
                ACourseDetailDTO aCourseDetailDTO = new ACourseDetailDTO();
                aCourseDetailDTO.setCourse(course);
                // 根据课程 ID 查询所有的章节
                List<CourseChapter> courseChapters = courseChapterMapper.selectAllByCourseId(course.getId());
                aCourseDetailDTO.setCourseChapters(courseChapters);
                aCourseDetailDTOS.add(aCourseDetailDTO);
            });
            aCourseTypeDetailDTO.setACourseDetailDTOS(aCourseDetailDTOS);
            resultList.add(aCourseTypeDetailDTO);
        });
        return DataResponseFactory.success(resultList);
    }

    @Override
    public DataResponseBody batchCreateCourseChapters(List<CourseChapterCreateDTO> courseChapterCreateDTOS) {
        courseChapterCreateDTOS.forEach(this::createCourseChapter);
        return DataResponseFactory.success();
    }

    /**
     * 将 courseChapterUpdateDTO 映射到 courseChapter
     *
     * @param courseChapterUpdateDTO 修改信息
     * @param courseChapter          courseChapter
     */
    private void extracted(CourseChapterUpdateDTO courseChapterUpdateDTO, CourseChapter courseChapter) {
        courseChapter.setCourseId(courseChapterUpdateDTO.getCourseId());
        courseChapter.setName(courseChapterUpdateDTO.getCourseChapterName());
        courseChapter.setSerialNumber(courseChapterUpdateDTO.getSerialNumber());
        courseChapter.setChapterType(courseChapterUpdateDTO.getChapterType());
        courseChapter.setIntroduction(courseChapterUpdateDTO.getIntroduction());
        courseChapter.setFileId(courseChapterUpdateDTO.getFileId());
    }


    /**
     * 根据 courseChapterCreateDTO 构建 CourseChapter
     *
     * @param courseChapterCreateDTO courseChapterCreateDTO
     * @return CourseChapter
     */
    private CourseChapter getCourseChapter(CourseChapterCreateDTO courseChapterCreateDTO) {
        return CourseChapter.builder()
                .id(null)
                .name(courseChapterCreateDTO.getCourseChapterName())
                .serialNumber(courseChapterCreateDTO.getSerialNumber())
                .chapterType(courseChapterCreateDTO.getChapterType())
                .courseId(courseChapterCreateDTO.getCourseId())
                .introduction(courseChapterCreateDTO.getIntroduction())
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .fileId(courseChapterCreateDTO.getFileId()).build();
    }

    /**
     * 因为在上传所有章节之前可能已经产生了该课程的学习记录,所以要对学习记录更新一遍
     *
     * @param course 课程对象
     */
    private void updateCourseSchedules(Course course) {
        course.setTotalChapters(course.getTotalChapters() + 1);
        course.setUpdateTime(LocalDateTime.now());
        List<CourseSchedule> courseSchedules = courseScheduleMapper.selectAllByCourseId(course.getId());
        courseSchedules.forEach(e -> {
            e.setTotalChapterNum(e.getTotalChapterNum() + 1);
            e.setSchedule((int) (((float) e.getLearnedChapterNum() / (float) e.getTotalChapterNum()) * 100));
            courseScheduleMapper.updateByPrimaryKeySelective(e);
        });
        courseMapper.updateByPrimaryKeySelective(course);
    }

    /**
     * 根据学习情况更新CourseSchedule
     *
     * @param courseId       课程 ID
     * @param userId         userID
     * @param course         课程
     * @param courseSchedule 课程进度
     */
    private void updateOrNotCourseSchedule(Long courseId, Long userId, Course course, CourseSchedule courseSchedule) {
        // 判断用户是否第一次学习此课程
        if (courseSchedule == null) {
            // 第一次学习此课程
            courseSchedule = new CourseSchedule(null, LocalDateTime.now(),
                    LocalDateTime.now(), course.getTotalChapters(),
                    1, (int) (((float) 1 / (float) course.getTotalChapters()) * 100),
                    0, courseId, userId);
            // 第一次学习就应该插入记录
            courseScheduleMapper.insertSelective(courseSchedule);
        } else {
            // 不是第一次学习此课程
            Integer learnedChapterNum = courseSchedule.getLearnedChapterNum();
            if (learnedChapterNum + 1 == courseSchedule.getTotalChapterNum()) {
                // 表示学完这章就 done
                courseSchedule.setDone(1);
                courseSchedule.setLearnedChapterNum(courseSchedule.getTotalChapterNum());
                courseSchedule.setSchedule(100);
                courseSchedule.setLastUpdateTime(LocalDateTime.now());
            } else if (learnedChapterNum.equals(courseSchedule.getTotalChapterNum())) {
                // 已经学完了
                // 更新最后一次学习时间
                courseSchedule.setLastUpdateTime(LocalDateTime.now());
            } else {
                // 加上这一张也没学完
                courseSchedule.setLearnedChapterNum(courseSchedule.getLearnedChapterNum() + 1);
                courseSchedule.setSchedule((int) (((float) courseSchedule.getLearnedChapterNum() / (float) course.getTotalChapters()) * 100));
                courseSchedule.setLastUpdateTime(LocalDateTime.now());
            }
            // 不是第一次学习就应该更新学习记录
            courseScheduleMapper.updateByPrimaryKeySelective(courseSchedule);
        }
    }

    /**
     * 生成CourseChapterDetailDTO
     *
     * @param courseId courseId
     * @param userId   userId
     * @param e        CourseChapter
     * @return CourseChapterDetailDTO
     */
    private CourseChapterDetailDTO getCourseChapterDetailDTO(Long courseId, Long userId, CourseChapter e) {
        // 根据 userId,课程 ID,章节 ID 查询学习记录
        CourseChapterSchedule courseChapterSchedule = courseChapterScheduleMapper.selectOneByUserIdAndChapterIdAndCourseId(userId, e.getId(), courseId);
        // 构建 CourseChapterDetailDTO
        CourseChapterDetailDTO courseChapterDetailDTO = new CourseChapterDetailDTO();
        // 根据文件 ID 查询 courseFile
        CourseFile courseFile = courseFileMapper.selectByPrimaryKey(e.getFileId());

        // 设置 ID
        courseChapterDetailDTO.setChapterId(e.getId());
        // 设置章节名字
        courseChapterDetailDTO.setChapterName(e.getName());
        // 设置章节序号
        courseChapterDetailDTO.setSerialNumber(e.getSerialNumber());
        // 设置章节简介
        courseChapterDetailDTO.setIntroduction(e.getIntroduction());
        // 设置章节内容类型
        courseChapterDetailDTO.setChapterType(e.getChapterType());
        // 设置课程 ID
        courseChapterDetailDTO.setCourseId(e.getCourseId());
        // 设置文件 URL
        courseChapterDetailDTO.setFileUrl(courseFile.getUrl());

        // 判断用户是否有学习记录
        if (courseChapterSchedule == null) {
            // 没有学习记录
            courseChapterDetailDTO.setLearned(0);
            courseChapterDetailDTO.setCreateTime(null);
            courseChapterDetailDTO.setUpdateTime(null);
        } else {
            // 有学习记录
            courseChapterDetailDTO.setLearned(1);
            courseChapterDetailDTO.setCreateTime(e.getCreateTime());
            courseChapterDetailDTO.setUpdateTime(e.getUpdateTime());
        }
        return courseChapterDetailDTO;
    }
}
