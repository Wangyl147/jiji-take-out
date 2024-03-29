package org.wangyl.jiji.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

// 全局异常捕获
// 处理含有以下注解的类
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody // 将 java 对象转为 json 格式的数据
@Slf4j
public class GlobalExceptionHandler {

    //SQL异常处理方法
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());

        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2]+"已存在";
            return R.error(msg);
        }
        return R.error("未知错误");
    }

    //自定义异常处理方法
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ce){
        log.error(ce.getMessage());
        return R.error(ce.getMessage());
    }
}
