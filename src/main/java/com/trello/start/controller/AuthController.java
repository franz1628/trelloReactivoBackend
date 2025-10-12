package com.trello.start.controller;

import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import com.trello.start.dto.RegisterRequest;
import com.trello.start.dto.RequestLogin;
import com.trello.start.dto.UserDto;
import com.trello.start.service.AuthService;
import com.trello.start.service.CookieService;
import com.trello.start.service.TokenBlacklistService;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;
    private final CookieService cookieService;
    private final TokenBlacklistService blacklistService;

    public AuthController(AuthService service, CookieService cookieService, TokenBlacklistService blacklistService) {
        this.service = service;
        this.cookieService = cookieService;
        this.blacklistService = blacklistService;
    }

    @PostMapping("/register")
    public Mono<ResponseEntity<UserDto>> register(@RequestBody RegisterRequest request) {
        return service.register(request)
                  .map(userDto -> ResponseEntity
                      .created(URI.create("/api/auth/" + userDto.getId()))
                      .body(userDto));
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<UserDto>> login(@RequestBody RequestLogin request ){
        return service.login(request).flatMap(responseLogin -> {
            ResponseEntity<UserDto> responseEntity = ResponseEntity.ok(responseLogin.getUser());
            ResponseCookie authCookie = cookieService.createAuthCookie(responseLogin.getToken(), responseLogin.getExpirationTime() / 1000);
            return Mono.just(ResponseEntity.ok()
                    .header("Set-Cookie", authCookie.toString())
                    .body(responseEntity.getBody()));
        });
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<String>> logout(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String jwtToken = extractJwtFromCookie(request);

        Mono<Void> revocationMono = jwtToken != null
                ? blacklistService.blacklistToken(jwtToken).then()
                : Mono.empty();

        ResponseCookie expiredCookie = cookieService.createAuthCookie("", 0);

        return revocationMono.then(Mono.just(ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
            .body("Session ended successfully")));
    }

    private String extractJwtFromCookie(ServerHttpRequest request) {
        var cookie = request.getCookies().getFirst("auth_token");
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }
}