package com.melosound.fit.domain.dto;

import lombok.Data;

@Data
public class JwtTokenDTO {
	private String accessToken;
	private String refreshToken;
}
