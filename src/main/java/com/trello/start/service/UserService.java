package com.trello.start.service;
import org.springframework.stereotype.Service;
import com.trello.start.model.User;
import com.trello.start.repository.UserRepository;
import reactor.core.publisher.Flux;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Flux<User> get(){
        return userRepository.findAll();
    }
}
