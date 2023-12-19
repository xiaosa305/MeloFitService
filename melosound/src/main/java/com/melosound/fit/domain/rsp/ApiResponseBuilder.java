package com.melosound.fit.domain.rsp;

import com.melosound.fit.utils.AESEncryptionUtils;

import cn.hutool.json.JSONUtil;

public class ApiResponseBuilder {
    private ApiResponse response;
    private AESEncryptionUtils encryptionUtils;

    public ApiResponseBuilder() {
        response = new ApiResponse();
        encryptionUtils = new AESEncryptionUtils();
    }

    public ApiResponseBuilder withCode(int code) {
        response.setCode(code);
        return this;
    }

    public ApiResponseBuilder withMessage(String message) {
        response.setMessage(message);
        return this;
    }

    public ApiResponseBuilder withData(Object data) {
    	if(encryptionUtils.getEnableEncryption()) {
    		response.setData(encryptionUtils.encrypt(JSONUtil.toJsonStr(data)));
    	}else{
    		response.setData(data);
    	}
        return this;
    }

    public ApiResponse build() {
        return response;
    }
}

