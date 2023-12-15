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
import com.melosound.fit.utils.AESEncryptionUtils;
import com.melosound.fit.utils.JwtUtils;

import cn.hutool.core.util.StrUtil;

@RestController
@RequestMapping("test")
public class TestController {
	private static final Logger logger = LoggerFactory.getLogger(TestController.class);
	
	@Autowired
	private AESEncryptionUtils aesUtil;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private JwtUtils jwtUtil;
	
	@PostMapping("test-decrypt")
	public String testDecrypt(@RequestBody String content) {
		logger.debug("test decrypt API");
		if (StrUtil.isNotEmpty(content) && aesUtil.getEnableEncryption()) {
			return aesUtil.decrypt(content);
		} else {
			return content;
		}
	}

	@PostMapping("test-encrypt")
	public String testEncrypt(@RequestBody String content) {
		logger.debug("test encrypt API: {}", content);
		if (StrUtil.isNotEmpty(content) && aesUtil.getEnableEncryption()) {
			return aesUtil.encrypt(content);
		} else {
			return content;
		}
	}
	
	@PostMapping("test")
	public String test(@RequestBody String requestBody,HttpServletRequest request) throws Exception{
		String sessionValue =  (String) redisTemplate.opsForValue().get("SessionId_" + request.getSession());
		return sessionValue;
	}
}

