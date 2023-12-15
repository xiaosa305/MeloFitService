package com.melosound.fit.domain.entity;

import java.util.Date;
import java.util.UUID;

import lombok.Data;

@Data
public class MeloUser {
	private String id;
	private String username;
	private String password;
	private String supervisorId;
	private String role;
	private String name;
	private String phone;
	private String address;
	private String email;
	private Date createTime;
	private Date modifyTime;
	
	public static class Builder{
		private String username;
		private String password;
		private String supervisorId;
		private String role;
		private String name;
		private String phone;
		private String address;
		private String email;
		public Builder setUsername(String username) {
			this.username = username;
			return this;
		}
		public Builder setPassword(String password) {
			this.password = password;
			return this;
		}
		public Builder setSupervisorId(String supervisorId) {
			this.supervisorId = supervisorId;
			return this;
		}
		public Builder setRole(String role) {
			this.role = role;
			return this;
		}
		public Builder setName(String name) {
			this.name = name;
			return this;
		}
		public Builder setPhone(String phone) {
			this.phone = phone;
			return this;
		}
		public Builder setAddress(String address) {
			this.address = address;
			return this;
		}
		public Builder setEmail(String email) {
			this.email = email;
			return this;
		}
		public MeloUser build() {
			MeloUser user = new MeloUser();
			user.setId(UUID.randomUUID().toString());
			Date now = new Date();
			user.setCreateTime(now);
			user.setModifyTime(now);
			user.setUsername(this.username);
			user.setPassword(this.password);
			user.setSupervisorId(this.supervisorId);
			user.setRole(this.role);
			user.setName(this.name);
			user.setAddress(this.address);
			user.setPhone(this.phone);
			user.setEmail(this.email);
			return user;
		}
	}
}
