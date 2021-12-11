package org.dubhe.data.course.service.impl;

import org.dubhe.biz.base.constant.ResponseCode;
import org.dubhe.biz.base.vo.DataResponseBody;
import org.dubhe.biz.log.enums.LogEnum;
import org.dubhe.biz.log.utils.LogUtil;
import org.dubhe.data.course.common.exception.FileUploadException;
import org.dubhe.data.course.dao.CourseFileMapper;
import org.dubhe.data.course.domain.CourseFile;
import org.dubhe.data.course.service.CourseFileService;
import org.dubhe.data.course.util.oss.OssUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.*;

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
@Transactional
public class CourseFileServiceImpl implements CourseFileService {

    private final static Logger LOGGER = LoggerFactory.getLogger(CourseFileService.class);
    static ExecutorService executorService;

    static {
        executorService = new ThreadPoolExecutor(5, 10, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5));
    }

    private final CourseFileMapper courseFileMapper;
    private final OssUtil ossUtil;

    public CourseFileServiceImpl(CourseFileMapper courseFileMapper, OssUtil ossUtil) {
        this.courseFileMapper = courseFileMapper;
        this.ossUtil = ossUtil;
    }

    @Override
    public DataResponseBody uploadFile(MultipartFile file) {

        String contentType = file.getContentType();

        Future<HashMap<String, String>> submit = executorService.submit(() -> {
            LogUtil.info(LogEnum.FILE_UTIL, "开始上传文件");
            HashMap<String, String> resultMap;
            try {
                resultMap = ossUtil.upload(file);
            } catch (Exception e) {
                LOGGER.error("{}线程上传文件名为{}的图片失败", Thread.currentThread().getId(), file.getName());
                e.printStackTrace();
                throw new FileUploadException(ResponseCode.ERROR, "上传文件失败");
            }
            return resultMap;
        });
        CourseFile courseFileRecord = null;
        try {
            // 获取结果集
            HashMap<String, String> uploadResult = submit.get();
            String url = uploadResult.get("url");
            String uri = uploadResult.get("uri");

            courseFileRecord = new CourseFile(null, file.getName(), contentType, url, LocalDateTime.now(), LocalDateTime.now(), uri);
            courseFileMapper.insertSelective(courseFileRecord);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return new DataResponseBody<>(courseFileRecord);
    }

}
