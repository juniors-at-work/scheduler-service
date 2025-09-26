package com.example.scheduler.adapters.mapper;

import com.example.scheduler.adapters.dto.ProfilePublicDto;
import com.example.scheduler.adapters.dto.ProfileResponse;
import com.example.scheduler.domain.model.Profile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProfileMapper {
    ProfileResponse toDto(Profile profile, String username);

    @Mapping(target = "fullName", source = "profile.fullName")
    @Mapping(target = "logo", source = "profile.logo")
    ProfilePublicDto toPublicDto(Profile profile);
}
