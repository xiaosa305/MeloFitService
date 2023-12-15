package com.melosound.fit.domain.dto;

import lombok.Data;

@Data
public class LoginSuccessDTO {
	private String accessToken;
	private String refreshToken;
}
