package com.melosound.fit.domain.cusenum;

public enum OperateType {
	QUERY(0),
	ADD_USER(1),
	DELETE_USER(2),
	UPDATE_USER_INFO(3),
	LOGIN(4),
	RESET_PASSWORD(5);
	
	private final int type;
	
	OperateType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
