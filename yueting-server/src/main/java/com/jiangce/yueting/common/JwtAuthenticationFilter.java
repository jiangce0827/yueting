package com.jiangce.yueting.common;

import com.jiangce.yueting.common.BaseContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Value("${yueting.jwt.secret}")
    private String jwtSecret;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info(request.getRequestURL().toString());
            // 从请求头提取 Token
            String token = extractToken(request);
            log.info("token:{}", token);
            // 校验 Token
            if (token != null && validateToken(token)) {
                // 解析 Token 获取用户信息并构建 Authentication 对象
                Authentication auth = buildAuthentication(token);
                // 将认证信息存储到 SecurityContext 中，供后续的 Spring Security 使用
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            // 继续处理请求
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            log.error("Token已过期", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token已过期");
        } catch (Exception e) {
            log.error("服务异常", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("服务异常，请稍后再试");
        }


    }

    /**
     * 从请求头中提取 Token
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    /**
     * 校验 Token 是否合法
     */
    private boolean validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        // 实现JWT校验逻辑（如签名、过期时间等）
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload() != null;
    }

    /**
     * 构建 Spring Security 的 Authentication 对象。
     */
    private Authentication buildAuthentication(String token) {
        // 生成密钥
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        // 获取 Claims
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // 获取用户权限信息（假设权限信息存储在 Claims 的 "authorities" 字段中）
        List<String> authorities = (List<String>) claims.get("authorities");
        log.info("claims:{}", claims);
        // 将权限字符串转换为 GrantedAuthority 对象
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        if (authorities != null) {
            grantedAuthorities = authorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }

        String userIdStr = claims.getSubject();
        Long userId = Long.valueOf(userIdStr);
        BaseContext.setCurrentId(userId);
        // 构建 Authentication 对象（用户名为用户 ID，密码为空，权限列表为空）
        return new UsernamePasswordAuthenticationToken(userId, null, grantedAuthorities);
    }
}
