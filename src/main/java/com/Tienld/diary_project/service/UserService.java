package com.Tienld.diary_project.service;

import com.Tienld.diary_project.converter.UserConverter;
import com.Tienld.diary_project.dto.request.UserCreateRequest;
import com.Tienld.diary_project.dto.response.UserResponse;
import com.Tienld.diary_project.entity.UserEntity;
import com.Tienld.diary_project.repository.UserRepository;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserConverter userConverter;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreateRequest userCreateRequest) {
        if(userRepository.existsByUsername(userCreateRequest.getUsername()) && userRepository.existsByEmail(userCreateRequest.getEmail())) {
            throw new RuntimeException("Username and email already exists");
        }
        UserEntity userEntity = userConverter.convertToUserEntity(userCreateRequest);
        userEntity.setPassword(passwordEncoder.encode(userCreateRequest.getPassword()));

        return userConverter.convertToUserResponse(userRepository.save(userEntity));
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(name).orElseThrow(() -> new RuntimeException("Username not found"));
        return userConverter.convertToUserResponse(user);
    }
}
