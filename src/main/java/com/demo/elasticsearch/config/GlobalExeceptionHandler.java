package com.demo.elasticsearch.config;

import com.demo.elasticsearch.Model.Result;
import com.demo.elasticsearch.Model.ResultGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author jky
 * @Time 2019/7/18 17:40
 * @Description
 */
@ControllerAdvice
@ResponseBody
public class GlobalExeceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(GlobalExeceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Result exceptionHandler(Exception ex) {
        logger.error(ex.getMessage(), ex);
        return ResultGenerator.genFailResult(ex.getMessage());
    }


}
