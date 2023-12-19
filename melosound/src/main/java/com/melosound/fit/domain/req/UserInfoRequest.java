package com.melosound.fit.domain.req;

import lombok.Data;

@Data
public class UserInfoRequest {
	private String username;
	private String password;
	private String name;
	private String phone;
	private String address;
	private String email;
}
