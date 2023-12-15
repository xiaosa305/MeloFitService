package com.melosound.fit.domain.dto;

import lombok.Data;

@Data
public class RegistUserInfoDTO {
	private String username;
	private String password;
	private String name;
	private String phone;
	private String address;
	private String email;
}
