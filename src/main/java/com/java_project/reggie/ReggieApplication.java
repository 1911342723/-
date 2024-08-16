package com.java_project.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;


//这个注解可以用来输出日志log方法
@Slf4j
//这个注解是开启请求扫描的，前端的请求都会被过滤看是否需要拦截
@ServletComponentScan
@SpringBootApplication
@EnableTransactionManagement//启用事务
//写一个启动类
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("项目启动成功！");
    }
}