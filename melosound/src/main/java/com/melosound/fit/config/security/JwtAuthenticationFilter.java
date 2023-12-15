package com.melosound.fit.config.security;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.melosound.fit.domain.CustomUser;
import com.melosound.fit.domain.cusenum.ResponseCode;
import com.melosound.fit.domain.response.ApiResponseBuilder;
import com.melosound.fit.service.impl.MeloUserServiceImpl;
import com.melosound.fit.utils.JwtUtils;
import com.melosound.fit.utils.RedisKeyUtil;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	private final int redisKeyExpireTIme = 30;
	private final TimeUnit redisKeyExpireTimeUnit = TimeUnit.MINUTES;

	@Resource
	private MeloUserServiceImpl userService;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private JwtUtils jwtUtil;
	
	@Autowired
	private RedisKeyUtil keyUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
			FilterChain filterChain) throws ServletException, IOException {
		HttpServletRequest request = servletRequest;
		String requestTokenHeader = request.getHeader(jwtUtil.getAccessTokenHeader());
		String jwtToken = StringUtil.EMPTY_STRING;
		String username = StringUtil.EMPTY_STRING;
		if(!StringUtil.isNullOrEmpty(requestTokenHeader) && requestTokenHeader.startsWith(jwtUtil.getTokenType())) {
			jwtToken = requestTokenHeader.substring(StringUtil.length(jwtUtil.getTokenType()));
			try {
				username = jwtUtil.getSubjectFromAccessToken(jwtToken);
			}catch(IllegalArgumentException e) {
				log.error("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
            	log.error("JWT Token has expired");
			}
		} else {
            logger.warn("JWT Token does not begin with Bearer String");
        }
		
		if(!StringUtil.isNullOrEmpty(username) && ObjectUtil.isNull(SecurityContextHolder.getContext().getAuthentication())) {
			CustomUser userDetails = userService.loadUserByUsername(username);
			if(ObjectUtil.isNotNull(userDetails) && jwtUtil.validateAccessToken(jwtToken)) {
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				String fitAuthorizationCode = request.getHeader("FitAuthorizationCode");
				if(!StringUtil.isNullOrEmpty(fitAuthorizationCode)) {
					redisTemplate.opsForValue().set(keyUtil.getfitAuthorizationCodeKey(username), fitAuthorizationCode);
					redisTemplate.expire(keyUtil.getfitAuthorizationCodeKey(username), 24,TimeUnit.HOURS);
				}
				String key = keyUtil.getUserSessionKey(request.getSession().getId());
				redisTemplate.opsForValue().set(key, userDetails.getMeloUser().getId());
				redisTemplate.expire(key, redisKeyExpireTIme,redisKeyExpireTimeUnit);
				
				
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		}
		filterChain.doFilter(servletRequest, servletResponse);
		
		String token = request.getHeader(jwtUtil.getAccessTokenHeader());
		if (StrUtil.isNotEmpty(token)) {
			String fitAuthorizationCode = request.getHeader("FitAuthorizationCode");
			if (StrUtil.isNotEmpty(token) && jwtUtil.validateAccessToken(token)) {
				username = jwtUtil.getSubjectFromAccessToken(token);
				if (StrUtil.isNotEmpty(username)) {
					CustomUser user = userService.loadUserByUsername(username);
					if (Objects.nonNull(user)) {
						UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
								user, null, user.getAuthorities());
						authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						if (StrUtil.isNotEmpty(fitAuthorizationCode)) {
							try {
								redisTemplate.opsForValue().set(keyUtil.getfitAuthorizationCodeKey(username), fitAuthorizationCode);
								//24小时后自动删除
								redisTemplate.expire(keyUtil.getfitAuthorizationCodeKey(username), 24,TimeUnit.HOURS);
							} catch (Exception e) {
								logger.error("redis save username-authorizationCode key value error: {}",
										e.getMessage());
								servletResponse.getWriter().write(JSONUtil.toJsonStr(new ApiResponseBuilder()
										.withCode(ResponseCode.SERVER_ERROR.getCode()).withMessage("服务器缓存异常，认证失败").withData(request).build()));
								return;
							}
						}
						String key = keyUtil.getUserSessionKey(request.getSession().getId());
						redisTemplate.opsForValue().set(key, user.getMeloUser().getId());
						redisTemplate.expire(key, redisKeyExpireTIme,redisKeyExpireTimeUnit);
						SecurityContextHolder.getContext().setAuthentication(authenticationToken);
						filterChain.doFilter(servletRequest, servletResponse);
						return;
					}
				}
			}
		}
		filterChain.doFilter(servletRequest, servletResponse);
//		servletResponse.setCharacterEncoding("UTF-8");
//		servletResponse.getWriter().write(JSONUtil.toJsonStr(new ApiResponseBuilder()
//				.withCode(ResponseCode.SUCCESS.getCode()).withMessage("TestCompleted测试成功").withData(request).build()));
	}
}
