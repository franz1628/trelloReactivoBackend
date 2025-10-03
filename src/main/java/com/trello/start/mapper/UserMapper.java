package com.trello.start.mapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.trello.start.dto.RegisterRequest;
import com.trello.start.dto.UserDto;
import com.trello.start.model.User;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );

    UserDto toDto(User model);

    User toEntity(UserDto model);
    User toEntity(RegisterRequest model);
} 