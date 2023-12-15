package com.melosound.fit.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.melosound.fit.service.impl.MeloUserServiceImpl;
import com.melosound.fit.utils.CustomMd5PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	http
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                        		.antMatchers("/api-superadmin/**").hasRole("SUPERADMIN")
                                .antMatchers("/api-admin/**").hasRole("ADMIN")
                                .antMatchers("/api-manager/**").hasRole("MANAGER")
                                .antMatchers("/api-fitter/**","/fitterWS").hasRole("FITTER")
                                .antMatchers("/clientWS","/api-client/**").hasRole("CLIENT")
                                .antMatchers("/mobileWS","/api-mobile/**").hasRole("MOBILE")
                                .antMatchers("/api-public/**").permitAll()
                                .antMatchers("/test/**","/api-jwttoken/**").anonymous()
                                .anyRequest().authenticated()
                )
                //.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .userDetailsService(userDetailsService())
                .csrf().disable();
    	//http.formLogin();
    	//http.httpBasic();
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
    	//return new BCryptPasswordEncoder();
		return new CustomMd5PasswordEncoder();
        // 返回您选择的密码编码器实例
    }

    @Bean
    public UserDetailsService userDetailsService() {
    	System.out.println("获取自定义用户服务");
        // 返回您自定义的用户详情服务实例
    	return new MeloUserServiceImpl();
    }
    
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
    	return new JwtAuthenticationFilter();
    }

    /*
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authentication -> {
			return null;
            // 在这里根据需要自定义身份验证逻辑
        };
    }
    */
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
