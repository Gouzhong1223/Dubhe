package org.dubhe.course.service.impl;

import org.dubhe.biz.base.service.UserContextService;
import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.biz.dataresponse.factory.DataResponseFactory;
import org.dubhe.course.dao.*;
import org.dubhe.course.domain.*;
import org.dubhe.course.domain.dto.CourseChapterCreateDTO;
import org.dubhe.course.domain.dto.CourseChapterDetailDTO;
import org.dubhe.course.service.CourseChapterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    public CourseChapterServiceImpl(CourseChapterScheduleMapper courseChapterScheduleMapper,
                                    UserContextService userContextService,
                                    CourseChapterMapper courseChapterMapper,
                                    CourseFileMapper courseFileMapper,
                                    CourseScheduleMapper courseScheduleMapper,
                                    CourseMapper courseMapper) {
        this.courseChapterScheduleMapper = courseChapterScheduleMapper;
        this.userContextService = userContextService;
        this.courseChapterMapper = courseChapterMapper;
        this.courseFileMapper = courseFileMapper;
        this.courseScheduleMapper = courseScheduleMapper;
        this.courseMapper = courseMapper;
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
        Long userId = userContextService.getCurUserId();
        // 添加章节学习记录
        CourseChapterSchedule courseChapterSchedule = new CourseChapterSchedule(null, LocalDateTime.now(), courseId, chapterId, userId);
        courseChapterScheduleMapper.insertSelective(courseChapterSchedule);

        // 根据 课程 ID 查询课程
        Course course = courseMapper.selectByPrimaryKey(courseId);

        // 更新课程预览看到的学习记录
        CourseSchedule courseSchedule = courseScheduleMapper.selectOneByUserIdAndCourseId(userId, courseId);
        courseSchedule = updateOrNotCourseSchedule(courseId, userId, course, courseSchedule);
        // 更新课程学习进度
        courseScheduleMapper.updateByPrimaryKeySelective(courseSchedule);

        // 查询课程章节
        CourseChapter courseChapter = courseChapterMapper.selectByPrimaryKey(chapterId);
        return DataResponseFactory.success(courseChapter);
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
     * @return CourseSchedule
     */
    private CourseSchedule updateOrNotCourseSchedule(Long courseId, Long userId, Course course, CourseSchedule courseSchedule) {
        // 判断用户是否第一次学习此课程
        if (courseSchedule == null) {
            // 第一次学习此课程
            courseSchedule = new CourseSchedule(null, LocalDateTime.now(),
                    LocalDateTime.now(), course.getTotalChapters(),
                    1, (int) (((float) 1 / (float) course.getTotalChapters()) * 100),
                    0, courseId, userId);
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
        }
        return courseSchedule;
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
