package com.melosound.fit.utils;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.Arrays;

public class CustomMd5PasswordEncoder implements PasswordEncoder{

	@Override
	public String encode(CharSequence rawPassword) {
		// TODO Auto-generated method stub
		// 进行一个md5加密
		return Arrays.toString(DigestUtils.md5Digest(rawPassword.toString().getBytes()));
	}

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		// TODO Auto-generated method stub
		// 通过md5校验
		return encodedPassword.equals(Arrays.toString(DigestUtils.md5Digest(rawPassword.toString().getBytes())));
	}
}
