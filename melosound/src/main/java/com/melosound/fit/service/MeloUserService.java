package com.melosound.fit.service;

import java.util.List;

import com.melosound.fit.domain.dto.Ret;
import com.melosound.fit.domain.dto.UserInfoDTO;
import com.melosound.fit.domain.po.MeloUser;
import com.melosound.fit.domain.req.RegistUserInfoRequest;
import com.melosound.fit.domain.req.ResetPasswordRequest;

public interface MeloUserService {
	//查单个
	public Ret userLogin(String username,String password);
	public Ret findUserById(String id);
	public Ret findUserByUsername(String username);
	//增
	public Ret registManager(RegistUserInfoRequest dto,String operatorId);
	public Ret registFitter(RegistUserInfoRequest dto,String operatorId);
	//删
	public Ret removeManagerById(String id,String operatorId);
	public Ret removeFitterById(String id,String operatorId);
	public Ret removeManagerByUsername(String username,String operatorId);
	public Ret removeFitterByUsername(String username,String operatorId);
	//改
	public Ret resetManagerPassword(ResetPasswordRequest dto,String operatorId);
	public Ret resetFitterPassword(ResetPasswordRequest dto,String operatorId);
	public Ret updateManager(UserInfoDTO dto,String operatorId);
	public Ret updateFitter(UserInfoDTO dto,String operatorId);
	//查集合
	public List<MeloUser> queryManagers(int pageSize,int offset);
	public List<MeloUser> queryFitters(int pageSize,int offset);
}
