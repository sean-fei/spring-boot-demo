package com.sean.activiti.exception;

import com.sean.activiti.util.Result;
import com.sean.activiti.util.Status;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    //处理自定义的异常
    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public Object customHandler(BaseException e){
//		e.printStackTrace();
        return Result.buildResult().status(e.getCode()).msg(e.getMessage());
    }

    //其他未处理的异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object exceptionHandler(Exception e){
        e.printStackTrace();
        return Result.buildResult().status(Status.FAIL).msg("系统错误");
    }
}