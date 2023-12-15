package com.melosound.fit.domain.cusenum;

public enum UserGender {
	FEMALE(0),
	MALE(1),
	SECRET(2);
	
	private final int gender;
	
	UserGender(int gender) {
        this.gender = gender;
    }

    public int getGender() {
        return gender;
    }
}
