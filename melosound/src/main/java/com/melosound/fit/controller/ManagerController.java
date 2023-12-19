package com.melosound.fit.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.melosound.fit.domain.cusenum.OperateResult;
import com.melosound.fit.domain.cusenum.OperateType;
import com.melosound.fit.domain.cusenum.ResponseCode;
import com.melosound.fit.domain.cusenum.ResultType;
import com.melosound.fit.domain.cusenum.UserRole;
import com.melosound.fit.domain.dto.Ret;
import com.melosound.fit.domain.dto.UserInfoDTO;
import com.melosound.fit.domain.po.MeloUser;
import com.melosound.fit.domain.po.MeloUserOperateLog;
import com.melosound.fit.domain.req.RegistUserInfoRequest;
import com.melosound.fit.domain.req.ResetPasswordRequest;
import com.melosound.fit.domain.rsp.ApiResponse;
import com.melosound.fit.domain.rsp.ApiResponseBuilder;
import com.melosound.fit.service.MeloUserOperateLogService;
import com.melosound.fit.service.MeloUserService;
import com.melosound.fit.utils.AESEncryptionUtils;
import com.melosound.fit.utils.JwtUtils;
import com.melosound.fit.utils.RedisKeyUtil;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import io.netty.util.internal.StringUtil;

@RestController
@RequestMapping("api-manager")
public class ManagerController {
	private static final Logger logger = LoggerFactory.getLogger(ManagerController.class);
	
	@Autowired
	private MeloUserService userService;
		
	@Autowired
	private MeloUserOperateLogService logService;
	
	@Autowired
	private AESEncryptionUtils aesUtil;
	
	@Autowired
	private RedisKeyUtil keyUtil;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	
	@PostMapping("registFitter")
	public ApiResponse registFitter(@RequestBody String requestStr,HttpServletRequest request)throws Exception {
		logger.info("Add Fitter API:");
		ResponseCode code = ResponseCode.SERVER_ERROR;
		String msg = StringUtil.EMPTY_STRING;
		Object data = null;
		String operator_id = (String) redisTemplate.opsForValue().get(keyUtil.getUserSessionKey(request.getSession().getId()));
		if(StringUtil.isNullOrEmpty(requestStr)) {
			requestStr = aesUtil.decrypt(requestStr);
			RegistUserInfoRequest dto = JSONUtil.toBean(requestStr, RegistUserInfoRequest.class);
			if(ObjectUtil.isNotNull(dto)) {
				Ret ret = userService.registFitter(dto,operator_id);
				if(ResultType.Success == ret.getResult()) {
					code = ResponseCode.SUCCESS;
					data = ret.getData();
				}else {
					code = ResponseCode.ERROR;
					msg = ret.getMsg();
				}
			}else {
				code = ResponseCode.BAD_REQUEST;
				msg = "注册信息格式错误，请求失败";
			}
		}else {
			code = ResponseCode.BAD_REQUEST;
			msg = "没有注册信息，请求失败";
		}
		return new ApiResponseBuilder()
				.withCode(code.getCode()).withMessage(msg).withData(data).build();
	}
	
	@PostMapping("resetManagePassword")
	public ApiResponse resetManagePassword(@RequestBody String requestStr,HttpServletRequest request) throws Exception{
		logger.info("resetManagePassword");
		String operator_id = (String) redisTemplate.opsForValue().get(keyUtil.getUserSessionKey(request.getSession().getId()));
		if(StringUtil.isNullOrEmpty(requestStr)) {
			requestStr = aesUtil.decrypt(requestStr);
			ResetPasswordRequest dto = JSONUtil.toBean(requestStr, ResetPasswordRequest.class);
			if(ObjectUtil.isNotNull(dto)) {
				Ret ret = userService.resetManagerPassword(dto, operator_id);
				if(ResultType.Success == ret.getResult()) {
					return new ApiResponseBuilder()
					.withCode(ResponseCode.SUCCESS.getCode()).build();
				}
				return new ApiResponseBuilder()
						.withCode(ResponseCode.ERROR.getCode()).withMessage(ret.getMsg()).build();
			}
		}
		return new ApiResponseBuilder()
				.withCode(ResponseCode.SERVER_ERROR.getCode()).withMessage("数据格式错误").build();
	}
	

	@PostMapping("resetFitterPassword")
	public ApiResponse resetFitterPassword(@RequestBody String requestStr,HttpServletRequest request) throws Exception{
		logger.info("resetFitterPassword");
		String operator_id = (String) redisTemplate.opsForValue().get(keyUtil.getUserSessionKey(request.getSession().getId()));
		if(StringUtil.isNullOrEmpty(requestStr)) {
			requestStr = aesUtil.decrypt(requestStr);
			ResetPasswordRequest dto = JSONUtil.toBean(requestStr, ResetPasswordRequest.class);
			if(ObjectUtil.isNotNull(dto)) {
				Ret ret = userService.resetFitterPassword(dto, operator_id);
				if(ResultType.Success == ret.getResult()) {
					return new ApiResponseBuilder()
					.withCode(ResponseCode.SUCCESS.getCode()).build();
				}
				return new ApiResponseBuilder()
						.withCode(ResponseCode.ERROR.getCode()).withMessage(ret.getMsg()).build();
			}
		}
		return new ApiResponseBuilder()
				.withCode(ResponseCode.SERVER_ERROR.getCode()).withMessage("数据格式错误").build();
	}
	
	@PostMapping("updateManagerInfo")
	public ApiResponse updateManagerInfo(@RequestBody String requestStr,HttpServletRequest request) throws Exception{
		logger.info("updateManagerInfo");
		String operator_id = (String) redisTemplate.opsForValue().get(keyUtil.getUserSessionKey(request.getSession().getId()));
		if(StringUtil.isNullOrEmpty(requestStr)) {
			requestStr = aesUtil.decrypt(requestStr);
			UserInfoDTO dto = JSONUtil.toBean(requestStr, UserInfoDTO.class);
			if(ObjectUtil.isNotNull(dto)) {
				Ret ret = userService.updateManager(dto, operator_id);
				if(ResultType.Success == ret.getResult()) {
					return new ApiResponseBuilder()
							.withData(dto)
							.withCode(ResponseCode.SUCCESS.getCode()).build();
				}
				return new ApiResponseBuilder()
						.withCode(ResponseCode.ERROR.getCode()).withMessage(ret.getMsg()).build();
			}
		}
		return new ApiResponseBuilder()
				.withCode(ResponseCode.SERVER_ERROR.getCode()).withMessage("数据格式错误").build();
	}
	
	
	@PostMapping("updateFitterInfo")
	public ApiResponse updateFitterInfo(@RequestBody String requestStr,HttpServletRequest request) throws Exception{
		logger.info("updateFitterInfo");
		String operator_id = (String) redisTemplate.opsForValue().get(keyUtil.getUserSessionKey(request.getSession().getId()));
		if(StringUtil.isNullOrEmpty(requestStr)) {
			requestStr = aesUtil.decrypt(requestStr);
			UserInfoDTO dto = JSONUtil.toBean(requestStr, UserInfoDTO.class);
			if(ObjectUtil.isNotNull(dto)) {
				Ret ret = userService.updateFitter(dto, operator_id);
				if(ResultType.Success == ret.getResult()) {
					return new ApiResponseBuilder()
							.withData(dto)
							.withCode(ResponseCode.SUCCESS.getCode()).build();
				}
				return new ApiResponseBuilder()
						.withCode(ResponseCode.ERROR.getCode()).withMessage(ret.getMsg()).build();
			}
		}
		return new ApiResponseBuilder()
				.withCode(ResponseCode.SERVER_ERROR.getCode()).withMessage("数据格式错误").build();
	}
	
	@PostMapping("deleteFitter")
	public ApiResponse deleteFitter() {
		ResponseCode code = ResponseCode.SERVER_ERROR;
		String msg = StringUtil.EMPTY_STRING;
		Object data = null;
		return new ApiResponseBuilder()
				.withCode(code.getCode()).withMessage(msg).withData(data).build();
	}
}
