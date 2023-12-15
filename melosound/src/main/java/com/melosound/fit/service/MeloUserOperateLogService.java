package com.melosound.fit.service;

import java.util.Map;

import com.melosound.fit.domain.cusenum.OperateResult;
import com.melosound.fit.domain.cusenum.OperateType;
import com.melosound.fit.domain.vo.Ret;

public interface MeloUserOperateLogService {
	
	public Ret addLog(String operatorId,String targetId,OperateType type,OperateResult result,String failureReson);
	public Ret findLog(long id);
	public Ret queryLogs(Map<String,Object> params);
	public Ret deleteLog(long id);
}
