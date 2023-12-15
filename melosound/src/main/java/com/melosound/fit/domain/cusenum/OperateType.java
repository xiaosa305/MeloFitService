package com.melosound.fit.domain.cusenum;

public enum OperateType {
	QUERY(0),
	ADD(1),
	DELETE(2),
	EDIT(3),
	LOGIN(4);
	
	private final int type;
	
	OperateType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
