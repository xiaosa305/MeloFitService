package com.melosound.fit.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.melosound.fit.domain.CustomUser;
import com.melosound.fit.domain.cusenum.OperateResult;
import com.melosound.fit.domain.cusenum.OperateType;
import com.melosound.fit.domain.cusenum.UserRole;
import com.melosound.fit.domain.dto.Ret;
import com.melosound.fit.domain.dto.UserInfoDTO;
import com.melosound.fit.domain.po.MeloUser;
import com.melosound.fit.domain.po.MeloUserBackup;
import com.melosound.fit.domain.po.MeloUserOperateLog;
import com.melosound.fit.domain.req.UserInfoRequest;
import com.melosound.fit.domain.req.ResetPasswordRequest;
import com.melosound.fit.mapper.MeloUserBackupMapper;
import com.melosound.fit.mapper.MeloUserMapper;
import com.melosound.fit.mapper.MeloUserOperateLogMapper;
import com.melosound.fit.service.MeloUserService;
import com.melosound.fit.utils.CustomMd5PasswordEncoder;

import cn.hutool.core.util.ObjectUtil;
import io.netty.util.internal.StringUtil;

@Service
public class MeloUserServiceImpl implements MeloUserService,UserDetailsService {
	private static final Logger logger = LoggerFactory.getLogger(MeloUserServiceImpl.class);
	private CustomMd5PasswordEncoder passwordEncoder;
	
	@Autowired
	private MeloUserMapper userMapper;
	
	@Autowired
	private MeloUserBackupMapper  backupMapper;
	
	@Autowired
	private MeloUserOperateLogMapper logMapper;
	
//	@Autowired
//	private Constant constant;
	
	public MeloUserServiceImpl() {
		this.passwordEncoder = new CustomMd5PasswordEncoder();
	}

	@Override
	public CustomUser loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("loadUserByUsername:{}",username);
		MeloUser user = userMapper.findUserByUsername(username);
		if(ObjectUtil.isNull(user)) {
			throw new UsernameNotFoundException("用户名不存在！");
		}
		logger.info("Check pass: {}({})", username, user.getRole());
		Collection<GrantedAuthority> collections = new ArrayList<GrantedAuthority>();
		collections.add(new SimpleGrantedAuthority(user.getRole()));
		return new CustomUser(user, collections);
	}

	@Override
	@Transactional
	public Ret userLogin(String username, String password) {
		logger.info("meloUserLogin: username({})",username);
		MeloUser user = userMapper.findUserByUsername(username);
		if(ObjectUtil.isNotNull(user) && user.getPassword().equals(passwordEncoder.encode(password))) {
			MeloUserOperateLog log = new MeloUserOperateLog.Builder()
			.setOperatorId(user.getId())
			.setOperateType(OperateType.LOGIN)
			.setOperateResult(OperateResult.SUCCESS)
			.build();
			logMapper.addLog(log);
			return new Ret.Builder().setData(user).Success();
		}
		return new Ret.Builder().setMsg("账号或密码错误").Failure();
	}
	
	@Override
	@Transactional
	public Ret findUserById(String id) {
		logger.info("findUserById: {}",id);
		MeloUser user = userMapper.findUserById(id);
		if(ObjectUtil.isNotNull(user)) {
			return new Ret.Builder().setData(user).Success();
		}
		return new Ret.Builder().setMsg("用户不存在").Failure();
	}

	@Override
	@Transactional
	public Ret findUserByUsername(String username) {
		logger.info("findUserByUsername: {}",username);
		MeloUser user = userMapper.findUserByUsername(username);
		if(ObjectUtil.isNotNull(user)) {
			return new Ret.Builder().setData(user).Success();
		}
		return new Ret.Builder().setMsg("用户不存在").Failure();
	}
	
	@Override
	@Transactional
	public Ret registManager(UserInfoRequest dto,String operatorId) {
		logger.info("registManager: username({})",dto.getUsername());
		MeloUser find = userMapper.findUserByUsername(dto.getUsername());
		if(ObjectUtil.isNull(find)) {
			MeloUser user = new MeloUser.Builder()
					.setUsername(dto.getUsername())
					.setPassword(passwordEncoder.encode(dto.getPassword()))
					.setName(dto.getName())
					.setAddress(dto.getAddress())
					.setPhone(dto.getPhone())
					.setEmail(dto.getEmail())
					.setSupervisorId(operatorId)
					.setRole(UserRole.MANAGER.getRole())
					.build();
			if(StringUtil.isNullOrEmpty(user.getName())) {
				user.setName(user.getUsername());
			}
			if(userMapper.addUser(user) > 0) {
				logMapper.addLog(new MeloUserOperateLog.Builder()
						.setOperatorId(operatorId)
						.setTargetId(user.getId())
						.setOperateType(OperateType.ADD_USER)
						.setOperateResult(OperateResult.SUCCESS)
						.build()
						);
				UserInfoDTO data = new UserInfoDTO();
				data.setId(user.getId());
				data.setUsername(user.getUsername());
				data.setName(user.getName());
				data.setAddress(user.getAddress());
				data.setPhone(user.getPhone());
				data.setEmail(user.getEmail());
				data.setRole(user.getRole());
				data.setCreateTime(user.getCreateTime());
				data.setModifyTIme(user.getModifyTime());
				return new Ret.Builder().setData(data).Success();
			}
		}
		return new Ret.Builder().setMsg("账号已存在").Failure();
	}
	
	@Override
	@Transactional
	public Ret registFitter(UserInfoRequest dto,String operatorId) {
		logger.info("registFitter: username({})",dto.getUsername());
		MeloUser find = userMapper.findUserByUsername(dto.getUsername());
		if(ObjectUtil.isNull(find)) {
			MeloUser user = new MeloUser.Builder()
					.setUsername(dto.getUsername())
					.setPassword(passwordEncoder.encode(dto.getPassword()))
					.setName(dto.getName())
					.setAddress(dto.getAddress())
					.setPhone(dto.getPhone())
					.setEmail(dto.getEmail())
					.setSupervisorId(operatorId)
					.setRole(UserRole.FITTER.getRole())
					.build();
			if(StringUtil.isNullOrEmpty(user.getName())) {
				user.setName(user.getUsername());
			}
			if(userMapper.addUser(user) > 0) {
				logMapper.addLog(new MeloUserOperateLog.Builder()
						.setOperatorId(operatorId)
						.setTargetId(user.getId())
						.setOperateType(OperateType.ADD_USER)
						.setOperateResult(OperateResult.SUCCESS)
						.build()
						);
				UserInfoDTO data = new UserInfoDTO();
				data.setId(user.getId());
				data.setUsername(user.getUsername());
				data.setName(user.getName());
				data.setAddress(user.getAddress());
				data.setPhone(user.getPhone());
				data.setEmail(user.getEmail());
				data.setRole(user.getRole());
				data.setCreateTime(user.getCreateTime());
				data.setModifyTIme(user.getModifyTime());
				return new Ret.Builder().setData(data).Success();
			}
		}
		return new Ret.Builder().setMsg("账号已存在").Failure();
	}

	@Override
	@Transactional
	public Ret removeManagerById(String id, String operatorId) {
		logger.info("removeManager id:({}), By user:({})",id,operatorId);
		MeloUser find = userMapper.findUserById(id);
		MeloUser operator = userMapper.findUserById(operatorId);
		if(ObjectUtil.isNotNull(find)) {
			if(!(UserRole.SUPERADMIN.getRole().equals(operator.getRole()))) {
				return new Ret.Builder().setMsg("没有权限").Failure();
			}
			if(userMapper.deleteUser(find.getId()) > 0) {
				MeloUserBackup backup = new MeloUserBackup.Builder().build(find, operatorId);
				backupMapper.addMeloUserBackup(backup);
				logMapper.addLog(new MeloUserOperateLog.Builder()
						.setOperatorId(operatorId)
						.setTargetId(find.getId())
						.setOperateType(OperateType.DELETE_USER)
						.setOperateResult(OperateResult.SUCCESS)
						.build()
						);
				return new Ret.Builder().Success();
			}
		}
		return new Ret.Builder().setMsg("账号不存在").Failure();
	}

	@Override
	@Transactional
	public Ret removeFitterById(String id, String operatorId) {
		logger.info("removeManager id:({}), By user Id:({})",id,operatorId);
		MeloUser find = userMapper.findUserById(id);
		MeloUser operator = userMapper.findUserById(operatorId);
		if(ObjectUtil.isNotNull(find)) {
			if(!(UserRole.SUPERADMIN.getRole().equals(operator.getRole()) || find.getSupervisorId().equals(operator.getId()))) {
				return new Ret.Builder().setMsg("没有权限").Failure();
			}
			if(userMapper.deleteUser(find.getId()) > 0) {
				MeloUserBackup backup = new MeloUserBackup.Builder().build(find, operatorId);
				backupMapper.addMeloUserBackup(backup);
				logMapper.addLog(new MeloUserOperateLog.Builder()
						.setOperatorId(operatorId)
						.setTargetId(find.getId())
						.setOperateType(OperateType.DELETE_USER)
						.setOperateResult(OperateResult.SUCCESS)
						.build()
						);
				return new Ret.Builder().Success();
			}
		}
		return new Ret.Builder().setMsg("账号不存在").Failure();
	}

	@Override
	@Transactional
	public Ret removeManagerByUsername(String username, String operatorId) {
		logger.info("removeManager username:({}), By user username:({})",username,operatorId);
		MeloUser find = userMapper.findUserByUsername(username);
		MeloUser operator = userMapper.findUserById(operatorId);
		if(ObjectUtil.isNotNull(find)) {
			if(!(UserRole.SUPERADMIN.getRole().equals(operator.getRole()))) {
				return new Ret.Builder().setMsg("没有权限").Failure();
			}
			if(userMapper.deleteUser(find.getId()) > 0) {
				MeloUserBackup backup = new MeloUserBackup.Builder().build(find, operatorId);
				backupMapper.addMeloUserBackup(backup);
				logMapper.addLog(new MeloUserOperateLog.Builder()
						.setOperatorId(operatorId)
						.setTargetId(find.getId())
						.setOperateType(OperateType.DELETE_USER)
						.setOperateResult(OperateResult.SUCCESS)
						.build()
						);
				return new Ret.Builder().Success();
			}
		}
		return new Ret.Builder().setMsg("账号不存在").Failure();
	}

	@Override
	@Transactional
	public Ret removeFitterByUsername(String username, String operatorId) {
		logger.info("removeManager username:({}), By user username:({})",username,operatorId);
		MeloUser find = userMapper.findUserByUsername(username);
		MeloUser operator = userMapper.findUserById(operatorId);
		if(ObjectUtil.isNotNull(find)) {
			if(!(UserRole.SUPERADMIN.getRole().equals(operator.getRole()) || find.getSupervisorId().equals(operator.getId()))) {
				return new Ret.Builder().setMsg("没有权限").Failure();
			}
			if(userMapper.deleteUser(find.getId()) > 0) {
				MeloUserBackup backup = new MeloUserBackup.Builder().build(find, operatorId);
				backupMapper.addMeloUserBackup(backup);
				logMapper.addLog(new MeloUserOperateLog.Builder()
						.setOperatorId(operatorId)
						.setTargetId(find.getId())
						.setOperateType(OperateType.DELETE_USER)
						.setOperateResult(OperateResult.SUCCESS)
						.build()
						);
				return new Ret.Builder().Success();
			}
		}
		return new Ret.Builder().setMsg("账号不存在").Failure();
	}
	
	@Override
	@Transactional
	public Ret resetManagerPassword(ResetPasswordRequest dto, String operatorId) {
		MeloUser target = userMapper.findUserByUsername(dto.getUsername());
		MeloUser operator = userMapper.findUserById(operatorId);
		if(ObjectUtil.isNull(target)) {
			return new Ret.Builder().setMsg("目标不存在").Failure();
		}
		if(!(operatorId.equals(target.getId()) || UserRole.SUPERADMIN.getRole().equals(operator.getRole()))) {
			return new Ret.Builder().setMsg("没有权限").Failure();
		}
		if(operatorId.equals(target.getId()) && !target.getPassword().equals(passwordEncoder.encode(dto.getOldPassword()))) {
			return new Ret.Builder().setMsg("旧密码错误").Failure();
		}
		target.setPassword(passwordEncoder.encode(dto.getNewPassword()));
		target.setModifyTime(new Date());
		if(userMapper.updateUser(target) <= 0) {
			return new Ret.Builder().setMsg("服务器异常").Failure();
		}
		try {
			logMapper.addLog(new MeloUserOperateLog.Builder()
					.setOperatorId(operatorId)
					.setTargetId(target.getId())
					.setOperateType(OperateType.RESET_PASSWORD)
					.setOperateResult(OperateResult.SUCCESS)
					.build()
					);
		}catch(Exception e) {
			logger.error("ResetManagerPassword add log Error: {}",e.getMessage());
		}
		UserInfoDTO data = new UserInfoDTO();
		data.setId(target.getId());
		data.setUsername(target.getUsername());
		data.setName(target.getName());
		data.setAddress(target.getAddress());
		data.setPhone(target.getPhone());
		data.setEmail(target.getEmail());
		data.setCreateTime(target.getCreateTime());
		data.setModifyTIme(target.getModifyTime());
		return new Ret.Builder().setData(data).Success();
	}

	@Override
	@Transactional
	public Ret resetFitterPassword(ResetPasswordRequest dto, String operatorId) {
		MeloUser target = userMapper.findUserByUsername(dto.getUsername());
		MeloUser operator = userMapper.findUserById(operatorId);
		if(ObjectUtil.isNull(target)) {
			return new Ret.Builder().setMsg("目标不存在").Failure();
		}
		if(!(operatorId.equals(target.getSupervisorId()) || operatorId.equals(target.getId()) || UserRole.SUPERADMIN.getRole().equals(operator.getRole()))) {
			return new Ret.Builder().setMsg("没有权限").Failure();
		}
		if(operatorId.equals(target.getId()) && !target.getPassword().equals(passwordEncoder.encode(dto.getOldPassword()))) {
			return new Ret.Builder().setMsg("旧密码错误").Failure();
		}
		target.setPassword(passwordEncoder.encode(dto.getNewPassword()));
		target.setModifyTime(new Date());
		if(userMapper.updateUser(target) <= 0) {
			return new Ret.Builder().setMsg("服务器异常").Failure();
		}
		try {
			logMapper.addLog(new MeloUserOperateLog.Builder()
					.setOperatorId(operatorId)
					.setTargetId(target.getId())
					.setOperateType(OperateType.RESET_PASSWORD)
					.setOperateResult(OperateResult.SUCCESS)
					.build()
					);
		}catch(Exception e) {
			logger.error("ResetManagerPassword add log Error: {}",e.getMessage());
		}
		UserInfoDTO data = new UserInfoDTO();
		data.setId(target.getId());
		data.setUsername(target.getUsername());
		data.setName(target.getName());
		data.setAddress(target.getAddress());
		data.setPhone(target.getPhone());
		data.setEmail(target.getEmail());
		data.setCreateTime(target.getCreateTime());
		data.setModifyTIme(target.getModifyTime());
		return new Ret.Builder().setData(data).Success();
	}

	@Override
	@Transactional
	public Ret updateManager(UserInfoRequest dto,String operatorId) {
		logger.info("modifyManager: username({})",dto.getUsername());
		MeloUser find = userMapper.findUserByUsername(dto.getUsername());
		MeloUser operator = userMapper.findUserById(operatorId);
		if(ObjectUtil.isNotNull(find)) {
			if(!(UserRole.SUPERADMIN.getRole().equals(operator.getRole()) || find.getId().equals(operatorId))) {
				return new Ret.Builder().setMsg("没有权限").Failure();
			}
			find.setName(dto.getName());
			find.setAddress(dto.getAddress());
			find.setPhone(dto.getPhone());
			find.setEmail(dto.getEmail());
			find.setModifyTime(new Date());
			if(userMapper.updateUser(find) > 0) {
				try {
					logMapper.addLog(new MeloUserOperateLog.Builder()
							.setOperatorId(operatorId)
							.setTargetId(find.getId())
							.setOperateType(OperateType.UPDATE_USER_INFO)
							.setOperateResult(OperateResult.SUCCESS)
							.build()
							);
				}catch(Exception e) {
					logger.error("ResetManagerPassword add log Error: {}",e.getMessage());
				}
				UserInfoDTO data = new UserInfoDTO();
				data.setId(find.getId());
				data.setUsername(find.getUsername());
				data.setName(find.getName());
				data.setAddress(find.getAddress());
				data.setPhone(find.getPhone());
				data.setEmail(find.getEmail());
				data.setCreateTime(find.getCreateTime());
				data.setModifyTIme(find.getModifyTime());
				return new Ret.Builder().setData(data).Success();
			}
		}
		return new Ret.Builder().setMsg("用户不存在").Failure();
	}

	@Override
	@Transactional
	public Ret updateFitter(UserInfoRequest dto,String operatorId) {
		logger.info("modifyManager: username({})",dto.getUsername());
		MeloUser find = userMapper.findUserByUsername(dto.getUsername());
		MeloUser operator = userMapper.findUserById(operatorId);
		if(ObjectUtil.isNotNull(find)) {
			if(!(UserRole.SUPERADMIN.getRole().equals(operator.getRole()) || find.getSupervisorId().equals(operatorId) || find.getId().equals(operatorId))) {
				return new Ret.Builder().setMsg("没有权限").Failure();
			}
			find.setName(dto.getName());
			find.setAddress(dto.getAddress());
			find.setPhone(dto.getPhone());
			find.setEmail(dto.getEmail());
			find.setModifyTime(new Date());
			if(userMapper.updateUser(find) > 0) {
				try {
					logMapper.addLog(new MeloUserOperateLog.Builder()
							.setOperatorId(operatorId)
							.setTargetId(find.getId())
							.setOperateType(OperateType.UPDATE_USER_INFO)
							.setOperateResult(OperateResult.SUCCESS)
							.build()
							);
				}catch(Exception e) {
					logger.error("ResetManagerPassword add log Error: {}",e.getMessage());
				}
				UserInfoDTO data = new UserInfoDTO();
				data.setId(find.getId());
				data.setUsername(find.getUsername());
				data.setName(find.getName());
				data.setAddress(find.getAddress());
				data.setPhone(find.getPhone());
				data.setEmail(find.getEmail());
				data.setCreateTime(find.getCreateTime());
				data.setModifyTIme(find.getModifyTime());
				return new Ret.Builder().setData(data).Success();
			}
		}
		return new Ret.Builder().setMsg("用户不存在").Failure();
	}
	
	@Override
	@Transactional
	public Ret updateFitterByManager(UserInfoRequest dto,String operatorId) {
		logger.info("modifyManager: username({})",dto.getUsername());
		MeloUser find = userMapper.findUserByUsername(dto.getUsername());
		MeloUser operator = userMapper.findUserById(operatorId);
		if(ObjectUtil.isNotNull(find)) {
			if(!(UserRole.SUPERADMIN.getRole().equals(operator.getRole()) || find.getSupervisorId().equals(operatorId))) {
				return new Ret.Builder().setMsg("没有权限").Failure();
			}
			find.setPassword(passwordEncoder.encode(dto.getPassword()));
			find.setName(dto.getName());
			find.setAddress(dto.getAddress());
			find.setPhone(dto.getPhone());
			find.setEmail(dto.getEmail());
			find.setModifyTime(new Date());
			if(userMapper.updateUser(find) > 0) {
				try {
					logMapper.addLog(new MeloUserOperateLog.Builder()
							.setOperatorId(operatorId)
							.setTargetId(find.getId())
							.setOperateType(OperateType.UPDATE_USER_INFO)
							.setOperateResult(OperateResult.SUCCESS)
							.build()
							);
				}catch(Exception e) {
					logger.error("ResetManagerPassword add log Error: {}",e.getMessage());
				}
				UserInfoDTO data = new UserInfoDTO();
				data.setId(find.getId());
				data.setUsername(find.getUsername());
				data.setName(find.getName());
				data.setAddress(find.getAddress());
				data.setPhone(find.getPhone());
				data.setEmail(find.getEmail());
				data.setCreateTime(find.getCreateTime());
				data.setModifyTIme(find.getModifyTime());
				return new Ret.Builder().setData(data).Success();
			}
		}
		return new Ret.Builder().setMsg("用户不存在").Failure();
	}

	@Override
	@Transactional
	public List<MeloUser> queryManagers(int pageSize,int offset) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("role", UserRole.MANAGER.getRole());
		if(-1 != pageSize && -1 != offset) {
			params.put("pageSize",pageSize);
			params.put("offset", offset);
		}
		List<MeloUser> users = userMapper.queryUser(params);
		return users;
	}

	@Override
	@Transactional
	public List<MeloUser> queryFitters(int pageSize,int offset) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("role", UserRole.FITTER.getRole());
		if(-1 != pageSize && -1 != offset) {
			params.put("pageSize",pageSize);
			params.put("offset", offset);
		}
		List<MeloUser> users = userMapper.queryUser(params);
		return users;
	}

	

	
}
