package com.trello.start.dto;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private String id;
    private String name;
    private String email;   
    private String username;
    private String photo;
    private String state; 
    private String role; 
    private Instant createdAt;
    private Instant updatedAt;
}
