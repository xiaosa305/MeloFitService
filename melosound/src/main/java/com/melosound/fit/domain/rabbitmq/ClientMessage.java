package com.melosound.fit.domain.rabbitmq;

import lombok.Data;

@Data
public class ClientMessage {
	public int code;
	public String msg;
	public Object data;
	public static class Builder{
		private int code;
		private String msg;
		private Object data;
		public Builder setCode(int code) {
			this.code = code;
			return this;
		}
		public Builder setMsg(String msg) {
			this.msg = msg;
			return this;
		}
		public Builder setData(Object data) {
			this.data = data;
			return this;
		}
		public ClientMessage build() {
			ClientMessage message = new ClientMessage();
			message.setCode(code);
			message.setMsg(msg);
			message.setData(data);
			return message;
		}
	}
}
