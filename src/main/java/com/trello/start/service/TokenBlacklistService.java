package com.trello.start.service;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.Duration;

@Service
public class TokenBlacklistService {

    private static final String BLACKLIST_PREFIX = "revoked_token:";
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final Duration JWT_EXPIRATION_DURATION = Duration.ofHours(1);

    public TokenBlacklistService(ReactiveRedisTemplate<String, String> reactiveRedisTemplate) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    public Mono<Boolean> blacklistToken(String token) {
        String key = BLACKLIST_PREFIX + token;
        return reactiveRedisTemplate.opsForValue().set(key, "true", JWT_EXPIRATION_DURATION);
    }

    public Mono<Boolean> isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return reactiveRedisTemplate.hasKey(key);
    }
}
