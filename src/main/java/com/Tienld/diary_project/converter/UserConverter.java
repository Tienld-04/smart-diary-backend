package com.Tienld.diary_project.converter;

import com.Tienld.diary_project.dto.request.UserCreateRequest;
import com.Tienld.diary_project.dto.response.UserResponse;
import com.Tienld.diary_project.entity.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    @Autowired
    private ModelMapper modelMapper;

    public UserResponse convertToUserResponse(UserEntity user) {
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);

        return userResponse;

    }
    public UserEntity convertToUserEntity(UserCreateRequest userCreateRequest) {
        UserEntity userEntity = modelMapper.map(userCreateRequest, UserEntity.class);
        return userEntity;
    }
}
