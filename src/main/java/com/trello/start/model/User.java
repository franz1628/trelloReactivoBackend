package com.trello.start.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "user")
public class User {
    @Id
    private String id;
    private String name;
    private String email;   
    private String username;
    private String password;
    private String photo;
    private String state; 
    private String role; 
    private String createdAt;
    private String updatedAt;
}
