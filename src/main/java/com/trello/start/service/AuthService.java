package com.trello.start.service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.trello.start.config.JwtUtils;
import com.trello.start.dto.RegisterRequest;
import com.trello.start.dto.RequestLogin;
import com.trello.start.dto.ResponseLogin;
import com.trello.start.dto.UserDto;
import com.trello.start.exception.DuplicateResourceException;
import com.trello.start.exception.ResourceNotFoundException;
import com.trello.start.mapper.UserMapper;
import com.trello.start.model.User;
import com.trello.start.repository.UserRepository;
import reactor.core.publisher.Mono;

@Service
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository repository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    public Mono<UserDto> register(RegisterRequest request) {
        if(request.getEmail() == null || request.getPassword() == null || request.getName() == null) {
            return Mono.error(new IllegalArgumentException("Email, password, and name must be provided"));
        }

        return repository.existsByEmail(request.getEmail().toLowerCase()).flatMap(exists -> {
            if(exists) {
                return Mono.error(new DuplicateResourceException("Email already in use"));
            }
            User entity = UserMapper.INSTANCE.toEntity(request);
            entity.setName(request.getName().toLowerCase());
            entity.setUsername(request.getUsername().toLowerCase());
            entity.setEmail(request.getEmail().toLowerCase());
            entity.setPassword(passwordEncoder.encode(request.getPassword()));
            entity.setPhoto("");

            Mono<User> newModel = repository.save(entity);
            
            Mono<UserDto> resModel = newModel.map(UserMapper.INSTANCE::toDto);
            return resModel;
        });
    }

    public Mono<ResponseLogin> login(RequestLogin request) {
        String password = request.getPassword();

        return repository.findByEmail(request.getEmail().toLowerCase())
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("User not found")))
            .flatMap(user -> {
                if (passwordEncoder.matches(password, user.getPassword())) {
                    ResponseLogin response = new ResponseLogin();
                    response.setToken(jwtUtils.generateToken(user));
                    response.setExpirationTime(jwtUtils.getExpirationTime());
                    response.setUser(UserMapper.INSTANCE.toDto(user));
                    return Mono.just(response);
                } else {
                    return Mono.error(new IllegalAccessException("Invalid credentials"));
                }
            });
    }
}
