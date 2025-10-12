package com.trello.start.security;

import com.trello.start.config.JwtUtils;
import com.trello.start.service.TokenBlacklistService;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends AuthenticationWebFilter {

    private final JwtUtils jwtUtils;
    private final TokenBlacklistService blacklistService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, TokenBlacklistService blacklistService) {
        super(new JwtReactiveAuthenticationManager());
        this.jwtUtils = jwtUtils;
        this.blacklistService = blacklistService;

        setServerAuthenticationConverter((ServerAuthenticationConverter) this::convert);
    }

    private static class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {
        @Override
        public Mono<Authentication> authenticate(Authentication authentication) {
            return Mono.just(authentication);
        }
    }

    private Mono<Authentication> convert(ServerWebExchange exchange) {
        String token = extractJwtFromCookie(exchange.getRequest());
        if (token == null) {
            return Mono.empty();
        }
        
        try {
            if (!jwtUtils.validateToken(token)) {
                return Mono.empty(); 
            }

            return blacklistService.isTokenBlacklisted(token)
                .flatMap(isBlacklisted -> {
                    if (isBlacklisted) {
                        return Mono.empty();
                    }

                    String username = jwtUtils.extractUsername(token);
                    String role = jwtUtils.extractRole(token);
                    String userId = jwtUtils.extractUserId(token);

                    if (username == null || role == null) {
                        return Mono.empty(); 
                    }

                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                    User userDetails = new User(username, "", Collections.singletonList(authority));

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, Collections.singletonList(authority));
                    
                    if (userId != null) {
                        authentication.setDetails(userId);
                    }

                    return Mono.just((Authentication) authentication);
                })
                .onErrorResume(error -> {
                    System.err.println("Error checking token blacklist: " + error.getMessage());
                    return Mono.empty();
                });
            
        } catch (Exception e) {
            return Mono.empty();
        }
    }

    private String extractJwtFromCookie(ServerHttpRequest request) {
        var cookie = request.getCookies().getFirst("auth_token");
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }
}
