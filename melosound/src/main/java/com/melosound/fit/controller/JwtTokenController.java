package com.melosound.fit.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.melosound.fit.domain.cusenum.ResponseCode;
import com.melosound.fit.domain.cusenum.ResultType;
import com.melosound.fit.domain.dto.Ret;
import com.melosound.fit.domain.req.RefreshTokenRequest;
import com.melosound.fit.domain.rsp.ApiResponse;
import com.melosound.fit.domain.rsp.ApiResponseBuilder;
import com.melosound.fit.service.JwtTokenService;
import com.melosound.fit.utils.AESEncryptionUtils;
import com.melosound.fit.utils.JwtUtils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import io.netty.util.internal.StringUtil;


@RestController
@RequestMapping("api-jwttoken")
public class JwtTokenController {
	private static final Logger logger = LoggerFactory.getLogger(JwtTokenController.class);
	@Autowired
	private JwtTokenService jwtTokenService;
	
	@Autowired
	private AESEncryptionUtils aesUtil;
	
	@Autowired
	private JwtUtils jwtUtil;
	
	@PostMapping("checkTokenEnable")
	public ApiResponse checkAccessTokenEnable(@RequestBody String requestBodyStr,HttpServletRequest request) {
		logger.info("checkAccessTokenEnable API:");
		requestBodyStr = aesUtil.decrypt(requestBodyStr);
		boolean pass = jwtUtil.validateAccessToken(requestBodyStr);
		if(pass) {
			return new ApiResponseBuilder().withCode(ResponseCode.SUCCESS.getCode()).build();
		}else {
			return new ApiResponseBuilder().withCode(ResponseCode.ERROR.getCode()).build();
		}
	}
	
	
	@PostMapping("refreshToken")
	public ApiResponse refreshToken(@RequestBody String requestBodyStr)throws Exception {
		logger.info("refreshToken: ");
		if(!StringUtil.isNullOrEmpty(requestBodyStr)) {
			requestBodyStr = aesUtil.decrypt(requestBodyStr);
			RefreshTokenRequest dto = JSONUtil.toBean(requestBodyStr, RefreshTokenRequest.class);
			if(ObjectUtil.isNotNull(dto) && jwtUtil.validaterefreshToken(dto.getRefreshToken())) {
				Ret ret = jwtTokenService.refreshAccessJwtToken(dto.getRefreshToken());
				if(ResultType.Success == ret.getResult()) {
					return new ApiResponseBuilder().withCode(ResponseCode.SUCCESS.getCode()).withData(ret.getData()).build();
				}
			}
			return new ApiResponseBuilder().withCode(ResponseCode.BAD_REQUEST.getCode()).withMessage("Token无效").build();
		}
		return new ApiResponseBuilder().withCode(ResponseCode.BAD_REQUEST.getCode()).withMessage("").build();
	}
}
