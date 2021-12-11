package org.dubhe.data.course.util.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.dubhe.data.course.config.oss.OssConfig;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * @Author : Gouzhong
 * @Blog : www.gouzhong1223.com
 * @Description : Oss工具类
 * @Date : create by QingSong in 2020-11-22 16:26
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : com.youcode.yxc.clockin.utils.oss
 * @ProjectName : clockin-youcode
 * @Version : 1.0.0
 */
@Component
public class OssUtil {
    private static final Logger logger = LoggerFactory.getLogger(OssUtil.class);

    private final OssConfig ossConfig;

    public OssUtil(OssConfig ossConfig) {
        this.ossConfig = ossConfig;
    }

    public HashMap<String, String> upload(MultipartFile file) {
        logger.info("=========>OSS文件上传开始：" + file.getName());
        String endpoint = ossConfig.getEndpoint();
        String accessKeyId = ossConfig.getAccessKeyId();
        String accessKeySecret = ossConfig.getAccessKeySecret();
        String bucketName = ossConfig.getBucketName();
        String fileHost = ossConfig.getFilehost();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(new Date());

        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
            //容器不存在，就创建
            if (!ossClient.doesBucketExist(bucketName)) {
                ossClient.createBucket(bucketName);
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                ossClient.createBucket(createBucketRequest);
            }
            String filename = file.getOriginalFilename();
            String filePath = getFilePath(filename);
            //上传文件
            PutObjectResult result = ossClient.putObject(new PutObjectRequest(bucketName, filePath, new ByteArrayInputStream(file.getBytes())));
            //设置权限 这里是公开读
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
            if (null != result) {
                logger.info("==========>OSS文件上传成功,OSS地址：" + filePath);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("url", "https://" + bucketName + "." + endpoint + "/" + filePath);
                hashMap.put("uri", filePath);
                return hashMap;
            }
        } catch (OSSException oe) {
            logger.error(oe.getMessage());
        } catch (ClientException ce) {
            logger.error(ce.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭
            ossClient.shutdown();
        }
        return null;
    }

    /**
     * 根据 URI 删除文件
     *
     * @param key URI
     */
    public void deleteFile(String key) {
        logger.info("=========>OSS文件删除开始：" + key);
        String endpoint = ossConfig.getEndpoint();
        String accessKeyId = ossConfig.getAccessKeyId();
        String accessKeySecret = ossConfig.getAccessKeySecret();
        String bucketName = ossConfig.getBucketName();
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        ossClient.deleteObject(bucketName, key);
    }

    /**
     * 根据文件名生成文件 URI
     *
     * @param sourceFileName
     * @return
     */
    private String getFilePath(String sourceFileName) {
        DateTime dateTime = new DateTime();
        return "files/" + dateTime.toString("yyyy")
                + "/" + dateTime.toString("MM") + "/"
                + dateTime.toString("dd") + "/" + System.currentTimeMillis() +
                RandomUtils.nextInt(new Random(100), 9999) + "." +
                StringUtils.substringAfterLast(sourceFileName, ".");
    }

    /**
     * 下载文件
     *
     * @param os
     * @param objectName
     * @throws IOException
     */
    public void exportOssFile(OutputStream os, String objectName) throws IOException {
        String endpoint = ossConfig.getEndpoint();
        String accessKeyId = ossConfig.getAccessKeyId();
        String accessKeySecret = ossConfig.getAccessKeySecret();
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
        OSSObject ossObject = ossClient.getObject(ossConfig.getBucketName(), objectName);
        // 读取文件内容。
        BufferedInputStream in = new BufferedInputStream(ossObject.getObjectContent());
        BufferedOutputStream out = new BufferedOutputStream(os);
        byte[] buffer = new byte[1024];
        int lenght = 0;
        while ((lenght = in.read(buffer)) != -1) {
            out.write(buffer, 0, lenght);
        }
        out.flush();
        out.close();
        in.close();
    }
}
