package com.skytakeout.handler;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.skytakeout.constant.MessageConstant;
import com.skytakeout.exception.BaseException;
import com.skytakeout.result.Result;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /*
    * 捕获业务异常
    * */
    @ExceptionHandler
    public Result exceptionHandler(BaseException e) {
        log.error("异常信息：{}", e.getMessage());
        return Result.error(e.getMessage());
    }

    /*
         * 捕获SQL异常
     * */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        String message = ex.getMessage();
        if(message.contains("Duplicate entry")) {
            String[] split = message.split(" ");
            String username = split[2];
            String msg = "Username " + username + MessageConstant.ALREADY_EXIST;
            return Result.error(msg);
        } else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

}
