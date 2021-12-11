package org.dubhe.data.course.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.dubhe.biz.base.constant.ResponseCode;
import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.data.course.service.CourseFileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
@Api(tags = "课程文件:课程文件管理")
@RequestMapping("file")
@RestController
public class DubheCourseFileController {

    private final CourseFileService courseFileService;

    public DubheCourseFileController(CourseFileService courseFileService) {
        this.courseFileService = courseFileService;
    }

    @PostMapping("upload")
    @ApiOperation("上传文件")
    public DataResponseBody uploadFile(@RequestParam("file") MultipartFile file) {
        if (file != null) {
            return courseFileService.uploadFile(file);
        }
        return new DataResponseBody(ResponseCode.ERROR, "文件不能为空");
    }

}
