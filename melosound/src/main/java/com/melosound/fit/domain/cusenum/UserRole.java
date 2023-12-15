package com.melosound.fit.domain.cusenum;

public enum UserRole {
	SUPERADMIN("ROLE_SUPERADMIN"),
	ADMIN("ROLE_ADMIN"),
	CLIENT("ROLE_CLIENT"),
	MANAGER("ROLE_MANAGER"),
	FITTER("ROLE_FITTER"),
	MOBILE("ROLE_MOBILE");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
