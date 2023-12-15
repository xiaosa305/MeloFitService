package com.melosound.fit.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.melosound.fit.domain.cusenum.ResponseCode;
import com.melosound.fit.domain.response.ApiResponse;
import com.melosound.fit.domain.response.ApiResponseBuilder;
@RestController
@RequestMapping("api-client")
public class ClientController {
	private static final Logger logger = LoggerFactory.getLogger(ClientController.class);
	
	@PostMapping("update-user-info")
	public ApiResponse updateUserInfo() {
		logger.info("Update user info API}");
		try {
			
		}catch(Exception e) {
			logger.error("Update user info",e.getMessage());
		}
		return new ApiResponseBuilder()
				.withCode(ResponseCode.SERVER_ERROR.getCode())
				.withMessage(ResponseCode.SERVER_ERROR.getMsg())
				.build();
	}
	
	@PostMapping("reset-password")
	public ApiResponse resetPassword() {
		logger.info("Update user info API");
		try {
			
		}catch(Exception e) {
			logger.error("Update user info",e.getMessage());
		}
		return new ApiResponseBuilder()
				.withCode(ResponseCode.SERVER_ERROR.getCode())
				.withMessage(ResponseCode.SERVER_ERROR.getMsg())
				.build();		
	}
}
