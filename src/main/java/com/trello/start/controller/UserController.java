package com.trello.start.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.trello.start.model.User;
import com.trello.start.service.UserService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Flux<User> get(){
        return userService.get();
    }

    @GetMapping("/me")
    public Mono<Map<String, Object>> getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token"));
        }

        Map<String, Object> userProfile = Map.of(
                "username", authentication.getName(),
                "roles", authentication.getAuthorities(),
                "message", "Authenticated successfully"
        );

        return Mono.just(userProfile);
    }   
}
