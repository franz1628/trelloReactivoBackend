package com.trello.start.model;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "user")
public class User {
    @Id
    private String id;
    private String name;
    private String email;   
    private String username;
    private String password;
    private String photo;
    private String state = "ACTIVE"; 
    private String role = "USER"; 
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
}
