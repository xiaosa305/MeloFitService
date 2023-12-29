package com.melosound.fit.domain.dto;

import java.util.Date;

import lombok.Data;

@Data
public class UserInfoDTO {
	private String id;
	private String username;
	private String name;
	private String phone;
	private String address;
	private String email;
	private String role;
	private Date createTime;
	private Date modifyTIme;
}
