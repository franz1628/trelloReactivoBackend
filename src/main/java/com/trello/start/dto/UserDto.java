package com.trello.start.dto;

import lombok.Data;

@Data
public class UserDto {
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
