package com.wz.pilipili.handler;


import com.wz.pilipili.exception.ConditionException;
import com.wz.pilipili.result.R;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 */
@ControllerAdvice  //
@Order(Ordered.HIGHEST_PRECEDENCE)  //优先级是最高的
public class CommonGlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)  //抛出了异常，都使用这个方法进行处理
    @ResponseBody
    public R<String> commonExceptionHandler(HttpServletRequest request, Exception e){
        String errorMsg = e.getMessage();//错误信息
        if(e instanceof ConditionException){
            String errorCode = ((ConditionException)e).getCode();//错误状态码
            return new R<>(errorCode, errorMsg);
        }else{
            return new R<>("500",errorMsg);
        }
    }
}
