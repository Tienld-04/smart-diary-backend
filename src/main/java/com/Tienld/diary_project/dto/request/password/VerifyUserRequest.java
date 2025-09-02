package com.Tienld.diary_project.dto.request.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyUserRequest {
    @NotBlank(message = "Username not null")
    private String username;

    @NotBlank(message = "Email not null")
    @Email(message = "Email format invalid")
    private String email;
}
