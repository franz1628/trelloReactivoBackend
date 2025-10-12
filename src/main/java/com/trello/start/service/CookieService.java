package com.trello.start.service;

import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {

    private final Environment environment;

    public CookieService(Environment environment) {
        this.environment = environment;
    }

    private boolean isProductionProfile() {
        return environment.acceptsProfiles(Profiles.of("prod", "staging"));
    }

    public ResponseCookie createAuthCookie(String tokenValue, long maxAgeSeconds) {
        return ResponseCookie.from("auth_token", tokenValue)
                .httpOnly(true)
                .secure(isProductionProfile())
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
    }

    public ResponseCookie createExpiredCookie() {
        return ResponseCookie.from("auth_token", "")
                .httpOnly(true)
                .secure(isProductionProfile())
                .sameSite("Strict")
                .path("/")
                .maxAge(0) 
                .build();
    }
}