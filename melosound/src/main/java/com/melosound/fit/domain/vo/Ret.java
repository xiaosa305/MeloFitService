package com.melosound.fit.domain.vo;

import com.melosound.fit.domain.cusenum.ResultType;

import lombok.Data;

@Data
public class Ret {
	private ResultType result;
	private String msg;
	private Object data;
	public static class Builder{
		private String msg;
		private Object data;
		public Builder setMsg(String msg) {
			this.msg = msg;
			return this;
		}
		public Builder setData(Object data) {
			this.data = data;
			return this;
		}
		public Ret Success() {
			Ret ret = new Ret();
			ret.result = ResultType.Success;
			ret.msg = this.msg;
			ret.data = this.data;
			return ret;
		}
		public Ret Failure() {
			Ret ret = new Ret();
			ret.result = ResultType.Failure;
			ret.msg = this.msg;
			ret.data = this.data;
			return ret;
		}
	}
}
