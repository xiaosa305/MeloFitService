package com.melosound.fit.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.melosound.fit.domain.cusenum.ResponseCode;
import com.melosound.fit.domain.cusenum.ResultType;
import com.melosound.fit.domain.dto.Ret;
import com.melosound.fit.domain.req.ResetPasswordRequest;
import com.melosound.fit.domain.rsp.ApiResponse;
import com.melosound.fit.domain.rsp.ApiResponseBuilder;
import com.melosound.fit.service.MeloUserService;
import com.melosound.fit.utils.AESEncryptionUtils;
import com.melosound.fit.utils.RedisKeyUtil;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import io.netty.util.internal.StringUtil;

@RestController
@RequestMapping("api-fitter")
public class FitterController {
	private static final Logger logger = LoggerFactory.getLogger(FitterController.class);
	
	@Autowired
	private MeloUserService userService;
	
	@Autowired
	private AESEncryptionUtils aesUtil;
	
	@Autowired
	private RedisKeyUtil keyUtil;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@PostMapping("resetFitterPassword")
	public ApiResponse resetFitterPassword(@RequestBody String requestStr,HttpServletRequest request) throws Exception{
		logger.info("resetFitterPassword");
		String operator_id = (String) redisTemplate.opsForValue().get(keyUtil.getUserSessionKey(request.getSession().getId()));
		if(!StringUtil.isNullOrEmpty(requestStr)) {
			requestStr = aesUtil.decrypt(requestStr);
			ResetPasswordRequest dto = JSONUtil.toBean(requestStr, ResetPasswordRequest.class);
			if(ObjectUtil.isNotNull(dto)) {
				Ret ret = userService.resetFitterPassword(dto, operator_id);
				if(ResultType.Success == ret.getResult()) {
					return new ApiResponseBuilder()	
							.withData(ret.getData())
							.withCode(ResponseCode.SUCCESS.getCode()).build();
				}
				return new ApiResponseBuilder()
						.withCode(ResponseCode.ERROR.getCode()).withMessage(ret.getMsg()).build();
			}
		}
		return new ApiResponseBuilder()
				.withCode(ResponseCode.SERVER_ERROR.getCode()).withMessage("数据格式错误").build();
	}
}
