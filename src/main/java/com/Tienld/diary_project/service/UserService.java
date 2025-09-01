package com.Tienld.diary_project.service;

import com.Tienld.diary_project.converter.UserConverter;
import com.Tienld.diary_project.dto.request.UserCreateRequest;
import com.Tienld.diary_project.dto.request.password.ChangePasswordRequest;
import com.Tienld.diary_project.dto.request.password.ResetPasswordRequest;
import com.Tienld.diary_project.dto.response.UserResponse;
import com.Tienld.diary_project.entity.UserEntity;
import com.Tienld.diary_project.exception.ApplicationException;
import com.Tienld.diary_project.exception.ErrorCode;
import com.Tienld.diary_project.repository.UserRepository;
import jakarta.validation.Valid;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserConverter userConverter;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreateRequest userCreateRequest) {
        if(userRepository.existsByUsername(userCreateRequest.getUsername()) || userRepository.existsByEmail(userCreateRequest.getEmail())) {
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
    @Transactional
    public String changePassword(ChangePasswordRequest changePasswordRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Username not found"));
        if(!passwordEncoder.matches(changePasswordRequest.getPassword(), user.getPassword())) {
            throw new ApplicationException(ErrorCode.PASSWORD_INVALID);
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userRepository.save(user);
        return "Change Password Success";

    }
    @Transactional
    public String resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String username = resetPasswordRequest.getUsername();
        String email = resetPasswordRequest.getEmail();
        UserEntity user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Username not found"));
        if (!user.getEmail().equals(email)) {
            throw new RuntimeException("Email does not match with username");
        }
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);
        return "Reset Password Success";
    }


}
