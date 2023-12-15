package com.melosound.fit.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.melosound.fit.domain.cusenum.ResponseCode;
import com.melosound.fit.domain.response.ApiResponse;
import com.melosound.fit.domain.response.ApiResponseBuilder;
import cn.hutool.core.convert.ConvertException;
import cn.hutool.json.JSONUtil;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(Exception.class)
	public ApiResponse handleException(Exception e) {
		printException(e);
		return new ApiResponseBuilder()
				.withCode(ResponseCode.SERVER_ERROR.getCode())
				.withMessage(ResponseCode.SERVER_ERROR.getMsg())
				.build();
	}
	
	@ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException e) {
		printException(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(JSONUtil.toJsonStr(new ApiResponseBuilder()
				.withCode(ResponseCode.BAD_REQUEST.getCode())
				.withMessage("用户信息错误")
				.build()));
    }
	
	@ExceptionHandler(ConvertException.class)
	public ApiResponse handleConvertException(ConvertException e) {
		printException(e);
		return new ApiResponseBuilder()
				.withCode(ResponseCode.BAD_REQUEST.getCode())
				.withMessage("请求参数错误")
				.build();
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ApiResponse handleRuntimeException(RuntimeException e) {
		printException(e);
		return new ApiResponseBuilder()
				.withCode(ResponseCode.SERVER_ERROR.getCode())
				.withMessage("请求错误")
				.build();
	}
	
	private void printException(Exception e) {
		StackTraceElement[] stackTrace = e.getStackTrace();
		 if (stackTrace.length > 0) {
		        StackTraceElement element = stackTrace[0];
		        String className = element.getClassName(); // 获取报错来源类名
		        String methodName = element.getMethodName(); // 获取报错来源方法名
		        logger.error("Error in class: {}", className);
		        logger.error("Error in method: {}", methodName);
		        logger.error("Error message: {}",e.getMessage());
		 }else {
			 logger.error("Default Error: {}", e.getMessage());
		 }
	}
//	@ExceptionHandler(HttpMessageNotReadableException.class)
//	public BasicResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
//		logger.error("HttpMessageNotReadableException: {}",e.getMessage());
//		return new BasicResponseBuilder()
//				.withCode(ResponseCode.BAD_REQUEST.getCode())
//				.withMessage("请求报文错误")
//				.build();
//	}
}
