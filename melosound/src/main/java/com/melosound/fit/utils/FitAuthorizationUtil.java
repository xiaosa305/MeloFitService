package com.melosound.fit.utils;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import cn.hutool.core.util.StrUtil;


@Component
public class FitAuthorizationUtil {
	private static final Logger logger = LoggerFactory.getLogger(FitAuthorizationUtil.class);
	private static final int CODE_LENGTH = 8;
	private static final long EXPIRATION_TIME_IN_SECONDS = 60 * 60;
	private static final String MOBILE_HEADER = "MobileFit_";
	private static final String FITAUTHORIZATIONCODE_HEADER = "FitAuthorizationCode_";
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	public String addFitAuthenticationCode(String subjetc) {
		removeFitAuthenticationCode(subjetc);
		long timestamp = System.currentTimeMillis();
		String code = subjetc.concat(String.valueOf(timestamp)).hashCode() + "";
		if (code.length() < CODE_LENGTH) {
			code = String.format("%08d", Integer.parseInt(code));
		} else {
			code = code.substring(0, CODE_LENGTH);
		}
		logger.debug("Fit Authentication Code: {}", code);
		redisTemplate.opsForValue().set(MOBILE_HEADER.concat(subjetc), code, EXPIRATION_TIME_IN_SECONDS, TimeUnit.SECONDS);
		redisTemplate.opsForValue().set(FITAUTHORIZATIONCODE_HEADER.concat(code), true, EXPIRATION_TIME_IN_SECONDS, TimeUnit.SECONDS);
		return code;
	}
	
	public boolean removeFitAuthenticationCode(String subjetc) {
		boolean result = true;
		String code = (String) redisTemplate.opsForValue().get(MOBILE_HEADER.concat(subjetc));
		if(StrUtil.isNotEmpty(code)) {
			if(Objects.nonNull(redisTemplate.opsForValue().get(FITAUTHORIZATIONCODE_HEADER.concat(code)))) {
				result &= redisTemplate.delete(FITAUTHORIZATIONCODE_HEADER.concat(code));
			}
			result &= redisTemplate.delete(MOBILE_HEADER.concat(subjetc));
		}
		return result;
	}
}
