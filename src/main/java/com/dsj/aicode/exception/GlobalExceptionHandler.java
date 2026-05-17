package com.dsj.aicode.exception;

import com.dsj.aicode.common.BaseResponse;
import com.dsj.aicode.common.ResultUtils;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author dongsijun
 * @date 2026/3/31  16:52
 */
@RestControllerAdvice
@Slf4j
@Hidden
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException businessException) {
        log.error("BusinessException", businessException);
        return ResultUtils.error(businessException.getCode(), businessException.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException runtimeException) {
        log.error("RuntimeException", runtimeException);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
