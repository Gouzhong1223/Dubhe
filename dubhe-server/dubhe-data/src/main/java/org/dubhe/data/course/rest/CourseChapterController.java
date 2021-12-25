package org.dubhe.data.course.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.dubhe.biz.base.constant.Permissions;
import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.biz.dataresponse.factory.DataResponseFactory;
import org.dubhe.data.course.domain.dto.CourseChapterCreateDTO;
import org.dubhe.data.course.domain.dto.CourseChapterUpdateDTO;
import org.dubhe.data.course.service.CourseChapterService;
import org.simpleframework.xml.core.Validate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        if (courseId == null || courseId == 0) {
            return DataResponseFactory.failed("courseId不能为空或0");
        }
        return courseChapterService.listAllCourseChapter(courseId);
    }

    @GetMapping("listAllCourseChapterA")
    @ApiOperation("管理员获取所有课程章节")
    @PreAuthorize(Permissions.COURSE_CHAPTER_UPDATE)
    DataResponseBody listAllCourseChapterA() {
        return courseChapterService.listAllCourseChapterA();
    }


    @GetMapping("studyCourseChapter/{courseId}/{chapterId}")
    @ApiOperation("学习章节")
    @PreAuthorize(Permissions.COURSE_CHAPTER)
    public DataResponseBody studyCourseChapter(@PathVariable Long chapterId, @PathVariable Long courseId) {
        if (chapterId == null || chapterId == 0 || courseId == null || courseId == 0) {
            return DataResponseFactory.failed("参数不能为空!");
        }
        return courseChapterService.studyCourseChapter(chapterId, courseId);
    }

    @PostMapping("createCourseChapter")
    @ApiOperation("上传课程章节")
    @PreAuthorize(Permissions.COURSE_CHAPTER_CREATE)
    public DataResponseBody createCourseChapter(@RequestBody @Validate CourseChapterCreateDTO courseChapterCreateDTO) {
        return courseChapterService.createCourseChapter(courseChapterCreateDTO);
    }

    @PostMapping("batchCreateCourseChapters")
    @ApiOperation("批量上传课程章节")
    @PreAuthorize(Permissions.COURSE_CHAPTER_CREATE)
    public DataResponseBody batchCreateCourseChapters(@RequestBody @Validate List<CourseChapterCreateDTO> courseChapterCreateDTOS) {
        if (CollectionUtils.isEmpty(courseChapterCreateDTOS)) {
            return DataResponseFactory.failed("章节列表不能为空!");
        }
        return courseChapterService.batchCreateCourseChapters(courseChapterCreateDTOS);
    }

    @PutMapping("updateCourseChapter")
    @ApiOperation("更新章节信息")
    @PreAuthorize(Permissions.COURSE_CHAPTER_UPDATE)
    public DataResponseBody updateCourseChapter(@RequestBody @Validate CourseChapterUpdateDTO courseChapterUpdateDTO) {
        return courseChapterService.updateCourseChapter(courseChapterUpdateDTO);
    }

    @DeleteMapping("deleteCourseChapter/{courseChapterId}")
    @ApiOperation("删除章节")
    @PreAuthorize(Permissions.COURSE_CHAPTER_DELETE)
    public DataResponseBody deleteCourseChapter(@PathVariable Long courseChapterId) {
        if (courseChapterId == null || courseChapterId == 0) {
            return DataResponseFactory.failed("参数不能为空!");
        }
        return courseChapterService.deleteCourseChapter(courseChapterId);
    }
}
