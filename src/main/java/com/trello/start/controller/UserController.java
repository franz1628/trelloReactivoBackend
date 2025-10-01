package com.trello.start.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.trello.start.model.User;
import com.trello.start.service.UserService;
import reactor.core.publisher.Flux;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Flux<User> get(){
        return userService.get();
    }
}
