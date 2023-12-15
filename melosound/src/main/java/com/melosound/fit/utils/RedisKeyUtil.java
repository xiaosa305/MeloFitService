package com.melosound.fit.utils;

import org.springframework.stereotype.Component;

@Component
public class RedisKeyUtil {
	public String getUserSessionKey(String sessionId) {
		StringBuilder builder = new StringBuilder();
		builder.append("OperatorSessionId_");
		builder.append(sessionId);
		return builder.toString();
	}
	
	public String getfitAuthorizationCodeKey(String username) {
		StringBuilder builder = new StringBuilder();
		builder.append("fitAuthorizationCode_");
		builder.append(username);
		return builder.toString();
	}
}
