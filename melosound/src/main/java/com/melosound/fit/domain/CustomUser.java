package com.melosound.fit.domain;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.melosound.fit.domain.po.MeloUser;


public class CustomUser extends User {
	private MeloUser user;

    public CustomUser(MeloUser user, Collection<? extends GrantedAuthority> authorities) {
        super(user.getUsername(), user.getPassword(), authorities);
        this.user = user;
    }
    
    public MeloUser getMeloUser() {
    	return this.user;
    }
}