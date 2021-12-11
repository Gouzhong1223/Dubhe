package org.dubhe.data.course.service.impl;

import org.dubhe.biz.base.constant.ResponseCode;
import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.biz.dataresponse.factory.DataResponseFactory;
import org.dubhe.biz.log.enums.LogEnum;
import org.dubhe.biz.log.utils.LogUtil;
import org.dubhe.data.course.dao.CourseMapper;
import org.dubhe.data.course.dao.CourseTypeMapper;
import org.dubhe.data.course.domain.Course;
import org.dubhe.data.course.domain.CourseType;
import org.dubhe.data.course.domain.dto.CourseTypeUpdateDTO;
import org.dubhe.data.course.service.CourseTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-04 19:28
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.service.impl
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@Service
@Transactional
public class CourseTypeServiceImpl implements CourseTypeService {

    private final CourseTypeMapper courseTypeMapper;
    private final CourseMapper courseMapper;

    public CourseTypeServiceImpl(CourseTypeMapper courseTypeMapper, CourseMapper courseMapper) {
        this.courseTypeMapper = courseTypeMapper;
        this.courseMapper = courseMapper;
    }

    @Override
    public DataResponseBody createCourseType(String courseTypeName) {
        CourseType courseTypeRecord = courseTypeMapper.selectOneByName(courseTypeName);
        if (courseTypeRecord != null) {
            return new DataResponseBody(ResponseCode.ERROR, "当前课程分类名字已存在,请更换名字!");
        }
        CourseType courseType = new CourseType(null, courseTypeName, LocalDateTime.now(), LocalDateTime.now());
        try {
            courseTypeMapper.insertSelective(courseType);
            LogUtil.info(LogEnum.COURSE, "新增课类型:" + courseTypeName);
        } catch (Exception e) {
            LogUtil.error(LogEnum.COURSE, "插入课程类型失败" + e.getMessage());
            e.printStackTrace();
        }
        return new DataResponseBody<>(courseType);
    }

    @Override
    public DataResponseBody updateCourseType(CourseTypeUpdateDTO courseTypeUpdateDTO) {
        CourseType courseType = courseTypeMapper.selectByPrimaryKey(courseTypeUpdateDTO.getCourseTypeId());
        if (courseType == null) {
            return DataResponseFactory.failed("课程分类不存在!");
        }
        courseType.setName(courseTypeUpdateDTO.getCourseTypeName());
        courseType.setUpdateTime(LocalDateTime.now());
        courseTypeMapper.updateByPrimaryKeySelective(courseType);
        LogUtil.info(LogEnum.COURSE, "更新课程分类信息:" + courseType);
        return DataResponseFactory.success(courseType);
    }

    @Override
    public DataResponseBody deleteCourseType(Long courseTypeId) {
        CourseType courseType = courseTypeMapper.selectByPrimaryKey(courseTypeId);
        if (courseType == null) {
            return DataResponseFactory.failed("课程分类不存在!");

        }
        Course course = courseMapper.selectOneByType(courseTypeId);
        if (course != null) {
            return DataResponseFactory.failed("有课程已经绑定了当前课程分类,不可删除!");
        }
        courseTypeMapper.deleteByPrimaryKey(courseTypeId);
        return DataResponseFactory.success(courseType);
    }

    @Override
    public DataResponseBody getAllCourseTypes() {
        return DataResponseFactory.success(courseTypeMapper.selectAll());
    }

}
