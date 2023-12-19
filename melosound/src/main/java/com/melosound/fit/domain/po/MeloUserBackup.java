package com.melosound.fit.domain.po;

import java.util.Date;

import lombok.Data;

@Data
public class MeloUserBackup {
	private long id;
	private String uuid;
	private String username;
	private String supervisorId;
	private String role;
	private String name;
	private String phone;
	private String address;
	private String email;
	private Date createTime;
	private Date modifyTime;
	private Date deletedTime;
	private String deletedUserId;
	public static class Builder{
		public MeloUserBackup build(MeloUser user,String deletedUserId) {
			MeloUserBackup backup = new MeloUserBackup();
			Date now = new Date();
			backup.setDeletedTime(now);
			backup.setUuid(user.getId());
			backup.setUsername(user.getUsername());
			backup.setSupervisorId(user.getSupervisorId());
			backup.setRole(user.getRole());
			backup.setName(user.getName());
			backup.setAddress(user.getAddress());
			backup.setPhone(user.getPhone());
			backup.setEmail(user.getEmail());
			backup.setCreateTime(user.getCreateTime());
			backup.setModifyTime(user.getModifyTime());
			backup.setDeletedUserId(deletedUserId);
			return backup;
		}
	}
}
