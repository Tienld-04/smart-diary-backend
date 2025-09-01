package com.Tienld.diary_project.dto.request.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "Username not null")
    private String username;
    @NotBlank(message = "Email not null")
    @Email(message = "Email is not in correct format")
    private String email;

    @NotBlank(message = "Password not null")
    @Size(min = 6)
    String newPassword;
}
