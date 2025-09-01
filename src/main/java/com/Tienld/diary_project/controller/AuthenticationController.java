package com.Tienld.diary_project.controller;

import com.Tienld.diary_project.dto.request.LoginRequest;
import com.Tienld.diary_project.dto.response.AuthenticationResponse;
import com.Tienld.diary_project.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
        @Autowired
        private AuthenticationService authenticationService;

        @PostMapping("/log-in")
        public ResponseEntity<AuthenticationResponse> logIn(@RequestBody LoginRequest loginRequest) {
                var res = authenticationService.authenticate(loginRequest);
                return ResponseEntity.ok(res);
        }
}
