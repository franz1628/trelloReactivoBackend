package com.trello.start.service;
import java.time.Instant;

import org.springframework.stereotype.Service;
import com.trello.start.dto.RegisterRequest;
import com.trello.start.dto.UserDto;
import com.trello.start.mapper.UserMapper;
import com.trello.start.model.User;
import com.trello.start.repository.UserRepository;
import reactor.core.publisher.Mono;

@Service
public class AuthService {
    private UserRepository repository;
    public AuthService(UserRepository repository){
        this.repository = repository;
    }

    public Mono<UserDto> register(RegisterRequest request) {
        if(request.getEmail() == null || request.getPassword() == null || request.getName() == null) {
            throw new IllegalArgumentException("Email, password, and name must be provided");
        }

        User entity = UserMapper.INSTANCE.toEntity(request);
        entity.setName(request.getName().toLowerCase());
        entity.setUsername(request.getUsername().toLowerCase());
        entity.setEmail(request.getEmail().toLowerCase());
        entity.setPassword(request.getPassword());
        entity.setCreatedAt(Instant.now().toString());
        entity.setUpdatedAt(Instant.now().toString());
        entity.setState("ACTIVO");
        entity.setRole("ADMIN");
        entity.setPhoto("");

        Mono<User> newModel = repository.save(entity);
        

        Mono<UserDto> resModel = newModel.map(UserMapper.INSTANCE::toDto);
        return resModel;
    }
}
