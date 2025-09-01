package com.Tienld.diary_project.dto.request.password;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordRequest {
    String username;
    String email;
}
