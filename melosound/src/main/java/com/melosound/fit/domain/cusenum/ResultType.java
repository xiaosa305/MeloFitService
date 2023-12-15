package com.melosound.fit.domain.cusenum;

public enum ResultType {
	Failure(0),
	Success(1);
	
private final int result;
	
	ResultType(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }
}
