package com.trello.start.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import com.trello.start.config.JwtUtils;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter extends AuthenticationWebFilter {
    private final JwtUtils jwtUtils;
    private final ReactiveUserDetailsService userDetailsService;

    private static final ReactiveAuthenticationManager NO_OP_MANAGER =
        authentication -> Mono.just(authentication);

    public JwtAuthenticationFilter(JwtUtils jwtUtils, ReactiveUserDetailsService userDetailsService) {
        super(NO_OP_MANAGER);
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;

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
        return userDetailsService.findByUsername(username)
                .map(user -> new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }
}
