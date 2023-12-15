package com.melosound.fit.utils;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;

@Component
public class AESEncryptionUtils {
	private static final Logger logger = LoggerFactory.getLogger(AESEncryptionUtils.class);
	
    private  static final boolean enableEncryption = false;
	
	private static final String SECRET_KEY = "0123456789ABHAEQ"; // 自定义密钥，需要保密
	
    private static final String INIT_VECTOR = "DYgjCEIMVrj2W9xN"; // 自定义初始向量，需要保密
	 
	private AES aes;
	
	public boolean getEnableEncryption() {
		return enableEncryption;
	}
	
	@Autowired
    public AESEncryptionUtils() {
    	aes =  new AES("CBC", "PKCS7Padding",
    			  // 密钥，可以自定义
    			SECRET_KEY.getBytes(),
    			  // iv加盐，按照实际需求添加
    			INIT_VECTOR.getBytes());
    }
	
	public String encrypt(String content) {
		logger.info("encrypt: {}", enableEncryption);
		if(enableEncryption) {
			return Base64.getEncoder().encodeToString(aes.encrypt(content));
		}
		return content;
	}
	
	public String decrypt(String encryptedInput) {
		if(enableEncryption) {
			byte[] decodedBytes = Base64.getDecoder().decode(encryptedInput);
			return aes.decryptStr(decodedBytes, CharsetUtil.CHARSET_UTF_8);
		}
		return encryptedInput;
	}
}
