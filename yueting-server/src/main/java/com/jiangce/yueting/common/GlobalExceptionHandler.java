package com.jiangce.yueting.common;

import com.jiangce.yueting.domain.result.Result;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 */
@ControllerAdvice
@ResponseBody //将对象转化为JOSN
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<String> exceptionHandler(DuplicateKeyException ex) {
        Throwable cause = ex.getCause();
        if (cause instanceof SQLIntegrityConstraintViolationException) {
            SQLIntegrityConstraintViolationException sqlEx = (SQLIntegrityConstraintViolationException) cause;
            log.error(sqlEx.getMessage());
            if(sqlEx.getMessage().contains("Duplicate entry")) {
                String msg = "已存在";
                return Result.error(msg);
            }
        }
        return Result.error("数据库操作失败");
    }
    /**
     * 当监管区域抛出SQLIntegrityConstraintViolationException时，进行接管
     * @param ex
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public Result<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());
        if(ex.getMessage().contains("Duplicate entry")) {
            String[] spilt = ex.getMessage().split(" ");
            String msg = spilt[2] + "已存在";
            return Result.error(msg);
        }
        return Result.error("未知错误");
    }

    @ExceptionHandler(CustomException.class)
    public Result<String> exceptionHandler(CustomException ex) {
        log.error(ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public Result<String> exceptionHandler(ExpiredJwtException ex) {
        log.error(ex.getMessage());
        return Result.error("身份认证已过期，请重新登录");
    }

    @ExceptionHandler(Exception.class)
    public Result<String> exceptionHandler(Exception ex) {
        log.error(ex.getMessage());
        ex.printStackTrace();
        return Result.error("系统异常，请稍后再试");
    }
}
