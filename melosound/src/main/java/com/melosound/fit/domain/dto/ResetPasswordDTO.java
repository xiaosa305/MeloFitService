package com.melosound.fit.domain.dto;

import lombok.Data;

@Data
public class ResetPasswordDTO {
	private String username;
	private String oldPassword;
	private String newPassword;
}
