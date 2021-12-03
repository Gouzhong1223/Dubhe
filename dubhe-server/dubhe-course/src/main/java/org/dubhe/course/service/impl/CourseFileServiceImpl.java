package org.dubhe.course.service.impl;

import org.dubhe.course.mapper.CourseFileMapper;
import org.dubhe.course.service.CourseFileService;
import org.springframework.stereotype.Service;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-03 22:44
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.service.impl
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@Service
public class CourseFileServiceImpl implements CourseFileService {


    private final CourseFileMapper courseFileMapper;

    public CourseFileServiceImpl(CourseFileMapper courseFileMapper) {
        this.courseFileMapper = courseFileMapper;
    }
}
