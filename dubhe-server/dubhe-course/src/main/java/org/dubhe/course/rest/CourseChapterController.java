package org.dubhe.course.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.dubhe.biz.base.constant.Permissions;
import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.course.service.CourseChapterService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description : 章节服务控制器
 * @Date : create by QingSong in 2021-12-06 20:38
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.rest
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@RestController
@RequestMapping("chapter")
@Api(tags = "章节:章节管理")
public class CourseChapterController {

    private final CourseChapterService courseChapterService;

    public CourseChapterController(CourseChapterService courseChapterService) {
        this.courseChapterService = courseChapterService;
    }

    @GetMapping("listAllCourseChapter/{courseId}")
    @ApiOperation("获取所有章节")
    @PreAuthorize(Permissions.COURSE_CHAPTER)
    public DataResponseBody listAllCourseChapter(@PathVariable Long courseId) {
        return courseChapterService.listAllCourseChapter(courseId);
    }
}
