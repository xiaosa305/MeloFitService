package com.melosound.fit.service;

import com.melosound.fit.domain.dto.Ret;

public interface JwtTokenService {
	public Ret refreshAccessJwtToken(String refreshToken);
}
