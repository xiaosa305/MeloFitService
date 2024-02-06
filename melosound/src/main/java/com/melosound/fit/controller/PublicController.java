package com.melosound.fit.controller;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.melosound.fit.domain.cusenum.OperateResult;
import com.melosound.fit.domain.cusenum.OperateType;
import com.melosound.fit.domain.cusenum.ResponseCode;
import com.melosound.fit.domain.cusenum.ResultType;
import com.melosound.fit.domain.dto.JwtTokenDTO;
import com.melosound.fit.domain.dto.Ret;
import com.melosound.fit.domain.dto.UserInfoDTO;
import com.melosound.fit.domain.po.MeloUser;
import com.melosound.fit.domain.req.LoginInfoRequest;
import com.melosound.fit.domain.req.UserInfoRequest;
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
@RequestMapping("api-public")
public class PublicController {
	private static final Logger logger = LoggerFactory.getLogger(PublicController.class);
		
	@Autowired
	private MeloUserOperateLogService logService;
	
	@Autowired
	private JwtUtils jwtUtil;
	
	@Autowired
	private AESEncryptionUtils aesUtil;
	
	@Autowired
	private RedisKeyUtil keyUtil;
	
	@Autowired
	private MeloUserService userService;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	

	
	
	@PostMapping("checkServerStatus")
    public ApiResponse checkServerStatus() {
		logger.info("checkServerStatus API:");
        return new ApiResponseBuilder().withCode(ResponseCode.SUCCESS.getCode()).withMessage("Server is running and connected successfully.").build();
    }
	

	@PostMapping("login")
	public ApiResponse login(@RequestBody String requestBodySty) throws Exception {
		logger.info("login API : {}",requestBodySty);
		if(StringUtil.isNullOrEmpty(requestBodySty)) {
			return new ApiResponseBuilder().withCode(ResponseCode.BAD_REQUEST.getCode()).withMessage("请求错误").build();
		}
		requestBodySty = aesUtil.decrypt(requestBodySty);
		LoginInfoRequest dto =JSONUtil.toBean(requestBodySty, LoginInfoRequest.class);
		if(ObjectUtil.isNull(dto)) {
			return new ApiResponseBuilder().withCode(ResponseCode.BAD_REQUEST.getCode()).withMessage("请求错误").build();
		}
		Ret ret = userService.userLogin(dto.getUsername(), dto.getPassword());
		if(ResultType.Success == ret.getResult()) {
			MeloUser user = (MeloUser) ret.getData();
			JwtTokenDTO response = new JwtTokenDTO();
			response.setAccessToken(jwtUtil.generateaccessToken(user.getUsername()));
			response.setRefreshToken(jwtUtil.generaterefreshToken(user.getUsername()));
			return new ApiResponseBuilder().withCode(ResponseCode.SUCCESS.getCode()).withMessage("登录成功")
					.withData(response).build();
		}
		return new ApiResponseBuilder().withCode(ResponseCode.ERROR.getCode()).withMessage(ret.getMsg()).build();
	}
	
	@PostMapping("getUserInfo")
	public ApiResponse getUserInfo(HttpServletRequest request)throws Exception {
		logger.info("getManagerInfo API:");
		String operator_id = (String) redisTemplate.opsForValue().get(keyUtil.getUserSessionKey(request.getSession().getId()));
		Ret ret = userService.findUserById(operator_id);
		if(ResultType.Success == ret.getResult()) {
			MeloUser user = (MeloUser) ret.getData();
			UserInfoDTO dto = new UserInfoDTO();
			dto.setId(user.getId());
			dto.setUsername(user.getUsername());
			dto.setName(user.getName());
			dto.setAddress(user.getAddress());
			dto.setPhone(user.getPhone());
			dto.setEmail(user.getEmail());
			dto.setRole(user.getRole());
			dto.setCreateTime(user.getCreateTime());
			dto.setModifyTIme(user.getModifyTime());
			return new ApiResponseBuilder().withCode(ResponseCode.SUCCESS.getCode()).withData(dto).build();
		}
		return new ApiResponseBuilder().withCode(ResponseCode.ERROR.getCode()).withMessage("用户不存在").build();
	}
}
