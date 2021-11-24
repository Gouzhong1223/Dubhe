package org.dubhe.course.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.dubhe.biz.base.constant.Permissions;
import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.course.service.CourseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description : 课程
 * @Date : create by QingSong in 2021-11-22 14:38
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.rest
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@Api(tags = "课程：课程管理")
@RestController
@RequestMapping("/course")
public class DubheCourseController {

    private final CourseService courseService;

    public DubheCourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("listAllCourses")
    @ApiOperation(value = "获取所有的课程")
    @PreAuthorize(Permissions.DATA)
    public DataResponseBody listAllCourses() {
        return new DataResponseBody(courseService.listAllCourses());
    }

}
