package com.trello.start.repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.trello.start.model.User;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    public Mono<User> findByEmail(String email);
}
