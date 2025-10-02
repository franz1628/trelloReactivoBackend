package com.trello.start.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.trello.start.dto.RegisterRequest;
import com.trello.start.dto.UserDto;
import com.trello.start.service.AuthService;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/api/auth")
public class AuthController {
    private AuthService service;

    public AuthController(AuthService service ){
        this.service = service;
    }

    @PostMapping("register")
    public Mono<ResponseEntity<UserDto>> register(@RequestBody RegisterRequest request) {
        return service.register(request).map(ResponseEntity::ok);
    }
}