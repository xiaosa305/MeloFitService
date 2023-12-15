package com.melosound.fit.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.melosound.fit.domain.entity.MeloUserBackup;

@Mapper
public interface MeloUserBackupMapper {
	public int countMeloUserBackups(Map<String,Object> params);
	public List<MeloUserBackup> queryMeloUserBackups(Map<String,Object> params);
	public int addMeloUserBackup(MeloUserBackup backup);
	public int deleteMeloUserBackup(long id);
}
