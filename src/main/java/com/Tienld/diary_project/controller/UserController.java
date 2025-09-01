package com.Tienld.diary_project.controller;

import com.Tienld.diary_project.dto.request.UserCreateRequest;
import com.Tienld.diary_project.dto.request.password.ChangePasswordRequest;
import com.Tienld.diary_project.dto.request.password.ResetPasswordRequest;
import com.Tienld.diary_project.dto.response.UserResponse;
import com.Tienld.diary_project.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        UserResponse userResponse = userService.createUser(userCreateRequest);
        return ResponseEntity.ok(userResponse);
    }
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        String changePassword = userService.changePassword(changePasswordRequest);
        return ResponseEntity.ok(changePassword);
    }
    @PostMapping("reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        String resetPassword = userService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok(resetPassword);
    }

    @GetMapping("/my-info")
    public ResponseEntity<UserResponse> getUser() {
        UserResponse userResponse = userService.getMyInfo();
        return ResponseEntity.ok(userResponse);
    }
}
