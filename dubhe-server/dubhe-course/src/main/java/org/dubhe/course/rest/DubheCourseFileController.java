package org.dubhe.course.rest;

import org.dubhe.course.service.CourseFileService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-03 22:43
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.rest
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
@RequestMapping("file")
@RestController
public class DubheCourseFileController {

    private final CourseFileService courseFileService;

    public DubheCourseFileController(CourseFileService courseFileService) {
        this.courseFileService = courseFileService;
    }

}
