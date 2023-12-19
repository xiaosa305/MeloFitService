package com.melosound.fit.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.melosound.fit.domain.cusenum.OperateResult;
import com.melosound.fit.domain.cusenum.OperateType;
import com.melosound.fit.domain.dto.Ret;
import com.melosound.fit.domain.po.MeloUserOperateLog;
import com.melosound.fit.mapper.MeloUserOperateLogMapper;
import com.melosound.fit.service.MeloUserOperateLogService;

import cn.hutool.core.util.ObjectUtil;

@Service
public class MeloUserOperateLogServiceImpl implements MeloUserOperateLogService {

	@Autowired
	private MeloUserOperateLogMapper logMapper;
	
	@Override 
	@Transactional
	public Ret addLog(String operatorId, String targetId, OperateType type, OperateResult result, String failureReson) {
		MeloUserOperateLog log = new MeloUserOperateLog.Builder()
				.setOperatorId(operatorId)
				.setTargetId(targetId)
				.setOperateType(type)
				.setOperateResult(result)
				.setfailureReson(failureReson)
				.build();
		if(ObjectUtil.isNull(log)) {
			return new Ret.Builder().setMsg("服务器异常").Failure();
		}
		if(logMapper.addLog(log) > 0) {
			return new Ret.Builder().setData(log).Success();
		}
		return new Ret.Builder().setMsg("服务器异常").Failure();
	}

	@Override
	public Ret findLog(long id) {
		// TODO Auto-generated method stub
		MeloUserOperateLog log = logMapper.findLog(id);
		if(ObjectUtil.isNotNull(log)) {
			return new Ret.Builder().setData(log).Success();
		}
		return new Ret.Builder().setMsg("log不存在").Failure();
	}

	@Override
	public Ret queryLogs(Map<String, Object> params) {
		List<MeloUserOperateLog> logs = logMapper.queryLogs(params);
		if(ObjectUtil.isNotEmpty(logs)) {
			return new Ret.Builder().setData(logs).Success();
		}
		return new Ret.Builder().setMsg("log不存在").Failure();
	}

	@Override
	@Transactional
	public Ret deleteLog(long id) {
		if(logMapper.deleteLog(id) > 0) {
			return new Ret.Builder().Success();
		}
		return new Ret.Builder().setMsg("服务器异常").Failure();
	}
}
