package com.example.scheduler.adapters.mapper;

import com.example.scheduler.adapters.dto.user.UserDto;
import com.example.scheduler.domain.model.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    UserDto toDto(User user);
}
