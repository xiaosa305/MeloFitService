package com.melosound.fit.service;

import com.melosound.fit.domain.vo.Ret;

public interface JwtTokenService {
	public Ret refreshAccessJwtToken(String refreshToken);
}
