package org.dubhe.course;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author : Gouzhong
 * @Blog : www.youcode123.com
 * @Description : Dubhe 课程服务启动类
 * @Date : create by QingSong in 2021-11-20 14:10
 * @Email : gouzhong1223@gmail.com
 * @Since : JDK 1.8
 * @PackageName : org.dubhe.course
 * @ProjectName : Dubhe-Course
 * @Version : 1.0.0
 */
@SpringBootApplication(scanBasePackages = "org.dubhe")
@MapperScan(basePackages = {"org.dubhe.**.mapper"})
public class DubheCourseApplication {
    public static void main(String[] args) {
        SpringApplication.run(DubheCourseApplication.class, args);
    }
}
