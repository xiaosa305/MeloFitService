package com.melosound.fit.domain.cusenum;

public enum ResponseCode {
	 	SUCCESS(200,"请求成功"),
	    ERROR(500,"请求失败"),
	    SERVER_ERROR(400,"服务器错误"),
	    NOT_PERMISSION(401,"权限不足"),
	    BAD_REQUEST(402,"请求错误"),
	    NOT_FOUND(404,"未找到");
		

	    private final int code;
	    private final String msg;

	    ResponseCode(int code,String msg) {
	        this.code = code;
	        this.msg = msg;
	    }

	    public int getCode() {
	        return code;
	    }
	    
	    public String getMsg() {
	    	return msg;
	    }
}
