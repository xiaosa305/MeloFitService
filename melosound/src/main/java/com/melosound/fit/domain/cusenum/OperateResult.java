package com.melosound.fit.domain.cusenum;

public enum OperateResult {
	FAILURE(0),
	SUCCESS(1);
	
	private final int result;
	
	OperateResult(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }
}
