package com.jiangce.yueting.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
// jwtUtils
@Component
public class JwtUtils {
    private static final Long EXPIRE_TIME = 3*86400000L; // 有效时间 3天
    private static final String CLAIM_KEY = "authorities";

    @Value("${yueting.jwt.secret}")
    private String instanceSecret;  // 实例变量用于注入

    private static SecretKey secretKey;  // 静态密钥

    @PostConstruct
    private void init() {
        // 在Bean初始化后构建密钥
        secretKey = Keys.hmacShaKeyFor(
                instanceSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public static String generateToken(String sub, List<String> authorities) {
        return Jwts.builder()
                .setSubject(sub)
                .claim(CLAIM_KEY, authorities)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(secretKey)
                .compact();
    }
}