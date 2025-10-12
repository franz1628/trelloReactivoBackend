package com.trello.start.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import com.trello.start.security.JwtAuthenticationFilter;
import reactor.core.publisher.Mono;

@Configuration
public class UserDetailsConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    public UserDetailsConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

@Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/board/**").hasRole("ADMIN")
                .pathMatchers("/api/users/me").authenticated()
                .anyExchange().permitAll()
            )
            .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((exchange, e) ->
                    Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                )
                .accessDeniedHandler((exchange, e) ->
                    Mono.fromRunnable(() -> exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN))
                )
            )
            .build();
    }

}