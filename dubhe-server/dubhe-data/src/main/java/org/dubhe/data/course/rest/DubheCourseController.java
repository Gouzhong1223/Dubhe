package org.dubhe.data.course.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.dubhe.biz.base.constant.Permissions;
import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.biz.dataresponse.factory.DataResponseFactory;
import org.dubhe.data.course.domain.dto.CourseCreateDTO;
import org.dubhe.data.course.domain.dto.CourseUpdateDTO;
import org.dubhe.data.course.service.CourseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


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
    @PreAuthorize(Permissions.COURSE)
    public DataResponseBody listAllCourses() {
        return courseService.listAllCourses();
    }

    @PostMapping("createCourse")
    @ApiOperation("创建课程")
    @PreAuthorize(Permissions.COURSE_CREATE)
    public DataResponseBody createCourse(@RequestBody @Validated CourseCreateDTO courseCreateDTO) {
        return courseService.createCourse(courseCreateDTO);
    }

    @PostMapping("updateCourse")
    @ApiOperation("更新课程")
    @PreAuthorize(Permissions.COURSE_UPDATE)
    public DataResponseBody updateCourse(@RequestBody @Validated CourseUpdateDTO courseUpdateDTO) {
        return courseService.updateCourse(courseUpdateDTO);
    }

    @DeleteMapping("updateCourse/{courseId}")
    @ApiOperation("删除课程信息")
    @PreAuthorize(Permissions.COURSE_DELETE)
    public DataResponseBody deleteCourse(@PathVariable Long courseId) {
        if (courseId == null || courseId == 0) {
            return DataResponseFactory.failed("courseId不能为空!");
        }
        return courseService.deleteCourse(courseId);
    }

}
