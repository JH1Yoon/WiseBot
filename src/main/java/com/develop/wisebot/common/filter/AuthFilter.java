package com.develop.wisebot.common.filter;

import com.develop.wisebot.common.jwt.JwtUtil;
import com.develop.wisebot.domain.user.entity.User;
import com.develop.wisebot.domain.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String bearerToken = request.getHeader(JwtUtil.AUTHORIZATION_HEADER);

        try {
            if (bearerToken != null) {
                String token = jwtUtil.checkToken(bearerToken);

                if (token != null && jwtUtil.validateToken(token)) {
                    Claims claims = jwtUtil.getUserInfoFromToken(token);

                    User user = userRepository.findByEmailOrThrow(claims.getSubject());

                    if (user != null) {
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                user, null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
                        );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }

            chain.doFilter(request, response);

        } catch (Exception e) {
            setErrorResponse(response, e);
        }
    }

    private void setErrorResponse(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        log.warn("JWT 인증 실패: {}", e.getMessage());

        String json = String.format("{\"status\":\"UNAUTHORIZED\",\"message\":\"%s\"}", e.getMessage());

        response.getWriter().write(json);
    }
}