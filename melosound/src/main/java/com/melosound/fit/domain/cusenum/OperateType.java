package com.melosound.fit.domain.cusenum;

public enum OperateType {
	QUERY(0,"Query"),
	ADD_USER(1,"Add"),
	DELETE_USER(2,"Delete"),
	UPDATE_USER_INFO(3,"Update"),
	LOGIN(4,"Login"),
	RESET_PASSWORD(5,"ResetPassword");
	
	private final int type;
	private final String name;
	
	OperateType(int type,String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }
    public String getName() {
    	return name;
    }
}
