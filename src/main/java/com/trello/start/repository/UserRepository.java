package com.trello.start.repository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import com.trello.start.model.User;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    
}
