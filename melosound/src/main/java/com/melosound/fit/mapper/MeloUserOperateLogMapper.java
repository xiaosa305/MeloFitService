package com.melosound.fit.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.melosound.fit.domain.po.MeloUserOperateLog;

@Mapper
public interface MeloUserOperateLogMapper {
	public int countLogs(Map<String,Object> params);
	/**s
	 * @param operatorId
	 * @param targetId
	 * @param operateType
	 * @param operateResult
	 * @param startCreateDate
	 * @param endCreateDate
	 * @return List<MeloUserOperateLog>
	 */
	public List<MeloUserOperateLog> queryLogs(Map<String,Object> params);
	/**
	 * @param id
	 * @return MeloUserOperateLog
	 */
	public MeloUserOperateLog findLog(long id);
	/**
	 * @param log
	 * @return int
	 */
	public int addLog(MeloUserOperateLog log);
	/**
	 * @param id
	 * @return int
	 */
	public int deleteLog(long id);
}
