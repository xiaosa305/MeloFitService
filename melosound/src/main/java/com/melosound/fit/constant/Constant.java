package com.melosound.fit.constant;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "constant")
@PropertySource("classpath:constant.properties")
public class Constant {
	private String roleAdmin;
	private String roleClientUser;
	private String roleMobileUser;
	private String roleSupperAdmin;
	
	private int responseStateSuccess;
	private int responseStateFail;
	
	private long rabbitMQMessageExpirationTime;
	
	private int pageSize;
}
