package com.melosound.fit.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.melosound.fit.domain.dto.JwtTokenDTO;
import com.melosound.fit.domain.entity.MeloUser;
import com.melosound.fit.domain.vo.Ret;
import com.melosound.fit.mapper.MeloUserMapper;
import com.melosound.fit.service.JwtTokenService;
import com.melosound.fit.service.MeloUserService;
import com.melosound.fit.utils.AESEncryptionUtils;
import com.melosound.fit.utils.JwtUtils;

import cn.hutool.core.util.ObjectUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtTokenServiceImpl implements JwtTokenService {

	@Autowired
	private JwtUtils jwtUtil;
	
	@Autowired
	private MeloUserMapper userMapper;
	
	
	@Override
	public Ret refreshAccessJwtToken(String refreshToken) {
		log.info("refreshAccessJwtToken API:");
		String username = jwtUtil.getSubjectFromRefreshToken(refreshToken);
		if(!StringUtil.isNullOrEmpty(username)) {
			MeloUser user = userMapper.findUserByUsername(username);
			if(ObjectUtil.isNotNull(user)) {
				log.info("refresh JwtToken Successed: {}", username);
				JwtTokenDTO dto = new JwtTokenDTO();
				dto.setAccessToken(jwtUtil.generateaccessToken(username));
				dto.setRefreshToken(jwtUtil.generaterefreshToken(username));
				return new Ret.Builder().setData(dto).Success();
			}
		}
		return new Ret.Builder().setMsg("用户不存在").Failure();
	}

}
