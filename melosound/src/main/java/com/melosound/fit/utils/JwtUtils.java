package com.melosound.fit.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {
	private final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
	private final String tokenType = "Bearer ";
    
    @Value("${jwt.accessSecret}")
    private String accessSecret;
    
    @Value("${jwt.refreshSecret}")
    private String refreshSecret;
    
    @Value("${jwt.accessTokenExpiration}")
    private Long accessTokenExpiration;
    
    @Value("${jwt.refreshTokenExpiration}")
    private Long refreshTokenExpiration;
    
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    
    public String generateaccessToken(String subject) {
    	return generateToken(subject,accessSecret,accessTokenExpiration * 1000);
    }
    
    public String generaterefreshToken(String subject) {
    	return generateToken(subject,refreshSecret,refreshTokenExpiration * 1000);
    }
    
    
    public String generateToken(String subject,String secret,long expiration) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Key signingKey = new SecretKeySpec(secret.getBytes(), signatureAlgorithm.getJcaName());
        String token = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(signingKey, signatureAlgorithm)
                .compact();
        return buildTokenByTyp(token);
    }

    public String getSubjectFromAccessToken(String token) {
    	token = getTokenByType(token);
    	Key signingKey = new SecretKeySpec(accessSecret.getBytes(), "HMACSHA256");
    	String username;
        Claims claims;
		try {
			claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
			username = claims.getSubject();
		} catch (Exception e) {
			username = null;
			logger.error("getSubjectFromAccessToken: {}", e.getMessage());
		}
        return username;
    }
    
    public String getSubjectFromRefreshToken(String token) {
    	token = getTokenByType(token);
    	Key signingKey = new SecretKeySpec(refreshSecret.getBytes(), "HMACSHA256");
    	String username;
        Claims claims;
		try {
			claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
			username = claims.getSubject();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			username = null;
			logger.error("getSubjectFromRefreshToken: {}", e.getMessage());
		}
        return username;
    }
    
    public boolean validateAccessToken(String token) {
        try {
        	token = getTokenByType(token);
        	Key signingKey = new SecretKeySpec(accessSecret.getBytes(), "HMACSHA256");
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
        	logger.error("validateAccessToken ({}): {}", token, e.getMessage());
            return false;
        }
    }
    
    public boolean validaterefreshToken(String token) {
        try {
        	token = getTokenByType(token);
        	Key signingKey = new SecretKeySpec(refreshSecret.getBytes(), "HMACSHA256");
            Jwts.parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
        	logger.error("validaterefreshToken: {}",e.getMessage());
            return false;
        }
    }
    
    public boolean isrefreshTokenExpired(String token) {
    	try {
    		token = getTokenByType(token);
    		Key signingKey = new SecretKeySpec(refreshSecret.getBytes(), "HMACSHA256");
        	Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Date expiration = claims.getExpiration();
            long currentTimeMillis = System.currentTimeMillis();
            long expirationTimeMillis = expiration.getTime();
            long remainingTimeMillis = expirationTimeMillis - currentTimeMillis;
            return remainingTimeMillis < 86400000; // 检查是否小于1天（1天的毫秒数为86400000）
    	}catch(Exception e) {
    		logger.error("isrefreshTokenExpired: {}",e.getMessage());
    		return false;
    	}
    }
    
    private String buildTokenByTyp(String jwt) {
    	return tokenType.concat(jwt);
    }
    
    private String getTokenByType(String jwt) {
    	return jwt.replaceAll(tokenType, "");
    }

    public String getAccessTokenHeader() {
    	return tokenHead;
    }
    
    public String getTokenType() {
    	return tokenType;
    }
}
