package com.melosound.fit.domain.po;

import java.util.Date;

import com.melosound.fit.domain.cusenum.OperateResult;
import com.melosound.fit.domain.cusenum.OperateType;

import io.netty.util.internal.StringUtil;
import lombok.Data;

@Data
public class MeloUserOperateLog {
	private long id;
	private String operatorId;
	private String targetId;
	private int operateType;
	private int operateResult;
	private String failureReson;
	private boolean deleted;
	private Date createTime;
	public static class Builder{
		private String operatorId;
		private String targetId;
		private OperateType operateType;
		private OperateResult operateResult;
		private String failureReson;
		public Builder setOperatorId(String operatorId) {
			this.operatorId = operatorId;
			return this;
		}
		public Builder setTargetId(String targetId) {
			this.targetId = targetId;
			return this;
		}
		public Builder setOperateType(OperateType type) {
			this.operateType = type;
			return this;
		}
		public Builder setOperateResult(OperateResult result) {
			this.operateResult = result;
			return this;
		}
		public Builder setfailureReson(String failureReson) {
			this.failureReson = failureReson;
			return this;
		}
		public MeloUserOperateLog build() {
			MeloUserOperateLog log = new MeloUserOperateLog();
			log.setOperatorId(this.operatorId);
			log.setTargetId(this.targetId);
			log.setOperateType(this.operateType.getType());
			log.setOperateResult(this.operateResult.getResult());
			log.setFailureReson(this.failureReson);
			log.setCreateTime(new Date());
			log.setDeleted(false);
			if(StringUtil.isNullOrEmpty(log.operatorId) && StringUtil.isNullOrEmpty(targetId)) {
				return null;
			}
			return log;
		}
	}
}
