package com.melosound.fit.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.melosound.fit.domain.cusenum.OperateResult;
import com.melosound.fit.domain.cusenum.OperateType;
import com.melosound.fit.domain.cusenum.ResponseCode;
import com.melosound.fit.domain.cusenum.ResultType;
import com.melosound.fit.domain.cusenum.UserRole;
import com.melosound.fit.domain.dto.LoginInfoDTO;
import com.melosound.fit.domain.dto.LoginSuccessDTO;
import com.melosound.fit.domain.entity.MeloUser;
import com.melosound.fit.domain.response.ApiResponse;
import com.melosound.fit.domain.response.ApiResponseBuilder;
import com.melosound.fit.domain.vo.Ret;
import com.melosound.fit.service.MeloUserOperateLogService;
import com.melosound.fit.service.MeloUserService;
import com.melosound.fit.utils.AESEncryptionUtils;
import com.melosound.fit.utils.JwtUtils;
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
	private MeloUserService meloUserService;

	
	
	@PostMapping("check-server-status")
    public ApiResponse checkServerStatus() {
        return new ApiResponseBuilder().withCode(ResponseCode.SUCCESS.getCode()).withMessage("Server is running and connected successfully.").build();
    }

	@PostMapping("login")
	public ApiResponse login(@RequestBody String requestBodySty) throws Exception {
		logger.info("login API : {}",requestBodySty);
		if(StringUtil.isNullOrEmpty(requestBodySty)) {
			return new ApiResponseBuilder().withCode(ResponseCode.BAD_REQUEST.getCode()).withMessage("请求错误").build();
		}
		requestBodySty = aesUtil.decrypt(requestBodySty);
		LoginInfoDTO dto =JSONUtil.toBean(requestBodySty, LoginInfoDTO.class);
		if(ObjectUtil.isNull(dto)) {
			return new ApiResponseBuilder().withCode(ResponseCode.BAD_REQUEST.getCode()).withMessage("请求错误").build();
		}
		Ret ret = meloUserService.userLogin(dto.getUsername(), dto.getPassword());
		if(ResultType.Success == ret.getResult()) {
			MeloUser user = (MeloUser) ret.getData();
			LoginSuccessDTO response = new LoginSuccessDTO();
			response.setAccessToken(jwtUtil.generateaccessToken(user.getUsername()));
			response.setRefreshToken(jwtUtil.generaterefreshToken(user.getUsername()));
			Ret logRet = logService.addLog(StringUtil.EMPTY_STRING, user.getId(), OperateType.LOGIN, OperateResult.SUCCESS,StringUtil.EMPTY_STRING);
			if(ResultType.Failure == logRet.getResult()) {
				logger.error("Login Completed Add Log Failure: {}",logRet.getMsg());
			}
			return new ApiResponseBuilder().withCode(ResponseCode.SUCCESS.getCode()).withMessage("登录成功")
					.withData(response).build();
		}
		return new ApiResponseBuilder().withCode(ResponseCode.ERROR.getCode()).withMessage(ret.getMsg()).build();
	}
	
	@PostMapping("loginAdministrator")
	public ApiResponse loginAdministrator(@RequestBody String requestStr) throws Exception {
		logger.info("loginAdministrator API");
		return null;
	}
}
