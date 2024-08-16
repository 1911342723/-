package com.java_project.reggie.common;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/*
* 全局异常处理
* */
//这里的注释，表示所有有花括号内注解的方法都会被拦截，进入该类查看是否需要处理异常
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
@ResponseBody
public class GlobalExceptionHandler {
    //加上这个注释就表示这个方法会处理该类型的异常，这里写的是插入的异常
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHander(SQLIntegrityConstraintViolationException e){
        log.info(e.getMessage());
        if(e.getMessage().contains("Duplicate entry")){
            String[] split = e.getMessage().split(" ");
            String mes = split[2] + "已存在";
            return R.error(mes);
        }
        return R.error("失败了");
    }

    //设置全局处理自定义异常信息
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHander(CustomException e){
        log.info(e.getMessage());
        return R.error(e.getMessage());
    }
}
