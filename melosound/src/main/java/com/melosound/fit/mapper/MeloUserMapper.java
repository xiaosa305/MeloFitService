package com.melosound.fit.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.melosound.fit.domain.po.MeloUser;

@Mapper
public interface MeloUserMapper {
	public int countUsers(Map<String,Object> params);
	public List<MeloUser> queryUser(Map<String,Object> params);
	public MeloUser findUserById(String id);
	public MeloUser findUserByUsername(String username);
	public int addUser(MeloUser user);
	public int deleteUser(String id);
	public int updateUser(MeloUser user);
}
