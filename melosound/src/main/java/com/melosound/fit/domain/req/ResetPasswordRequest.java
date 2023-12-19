package com.melosound.fit.domain.req;

import lombok.Data;

@Data
public class ResetPasswordRequest {
	private String username;
	private String oldPassword;
	private String newPassword;
}
