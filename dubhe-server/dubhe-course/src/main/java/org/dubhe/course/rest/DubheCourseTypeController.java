package org.dubhe.course.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.dubhe.biz.base.constant.Permissions;
import org.dubhe.biz.base.constant.ResponseCode;
import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.course.service.CourseTypeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-04 19:26
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.rest
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@RestController
@RequestMapping("courseType")
@Api(tags = "课程类型:课程类型管理")
public class DubheCourseTypeController {


    private final CourseTypeService courseTypeService;

    public DubheCourseTypeController(CourseTypeService courseTypeService) {
        this.courseTypeService = courseTypeService;
    }

    @PostMapping("createCourseType")
    @ApiOperation("创建课程分类")
    @PreAuthorize(Permissions.COURSE_TYPE_CREATE)
    public DataResponseBody createCourseType(@RequestParam String courseTypeName) {
        if (courseTypeName == null || courseTypeName.equals("")) {
            return new DataResponseBody(ResponseCode.ERROR, "课程分类名字不能为空!");
        }
        return courseTypeService.createCourseType(courseTypeName);
    }
}
