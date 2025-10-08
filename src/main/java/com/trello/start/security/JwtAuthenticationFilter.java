package com.trello.start.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.trello.start.config.JwtUtils;
import reactor.core.publisher.Mono;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends AuthenticationWebFilter {
    private final JwtUtils jwtUtils;

    private static final ReactiveAuthenticationManager NO_OP_MANAGER =
        authentication -> Mono.just(authentication);

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        super(NO_OP_MANAGER);
        this.jwtUtils = jwtUtils;
        setServerAuthenticationConverter((ServerAuthenticationConverter) this::convert);
    }

    private Mono<Authentication> convert(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Mono.empty();
        }
        
        String token = authHeader.substring(7);
        if (!jwtUtils.validateToken(token)) {
            return Mono.empty();
        }
        
        String username = jwtUtils.extractUsername(token);
        String role = jwtUtils.extractRole(token);
        String userId = jwtUtils.extractUserId(token);
        String roleWithPrefix = "ROLE_" + role;
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleWithPrefix);
        User userDetails = new User(username, "", Collections.singletonList(authority));

        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(userDetails, null, Collections.singletonList(authority));
        
        authentication.setDetails(userId);
        
        return Mono.just(authentication);
    }
}
