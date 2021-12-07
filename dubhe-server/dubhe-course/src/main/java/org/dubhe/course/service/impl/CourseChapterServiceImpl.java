package org.dubhe.course.service.impl;

import org.dubhe.biz.base.service.UserContextService;
import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.biz.dataresponse.factory.DataResponseFactory;
import org.dubhe.course.dao.CourseChapterMapper;
import org.dubhe.course.dao.CourseChapterScheduleMapper;
import org.dubhe.course.dao.CourseFileMapper;
import org.dubhe.course.domain.CourseChapter;
import org.dubhe.course.domain.CourseChapterSchedule;
import org.dubhe.course.domain.CourseFile;
import org.dubhe.course.domain.dto.CourseChapterDetailDTO;
import org.dubhe.course.service.CourseChapterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public CourseChapterServiceImpl(CourseChapterScheduleMapper courseChapterScheduleMapper, UserContextService userContextService, CourseChapterMapper courseChapterMapper, CourseFileMapper courseFileMapper) {
        this.courseChapterScheduleMapper = courseChapterScheduleMapper;
        this.userContextService = userContextService;
        this.courseChapterMapper = courseChapterMapper;
        this.courseFileMapper = courseFileMapper;
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
