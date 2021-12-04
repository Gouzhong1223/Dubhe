package org.dubhe.course.service;

import org.dubhe.biz.base.vo.DataResponseBody;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description :
 * @Date : create by QingSong in 2021-12-03 22:44
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course.service
 * @ProjectName : Dubhe
 * @Version : 1.0.0
 */
public interface CourseFileService {
    /**
     * 上传文件
     *
     * @param file 文件
     * @return DataResponseBody
     */
    DataResponseBody uploadFile(MultipartFile file);
}
