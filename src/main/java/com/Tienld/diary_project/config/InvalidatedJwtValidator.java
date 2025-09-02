package com.Tienld.diary_project.config;

import com.Tienld.diary_project.repository.InvalidateTokenRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Configuration
public class InvalidatedJwtValidator implements OAuth2TokenValidator<Jwt> {

    private final InvalidateTokenRepository invalidateTokenRepository;

    public InvalidatedJwtValidator(InvalidateTokenRepository invalidateTokenRepository) {
        this.invalidateTokenRepository = invalidateTokenRepository;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        String jti = jwt.getId(); // "jti" claim
        if (jti != null && invalidateTokenRepository.existsByJti(jti)) {
            OAuth2Error error = new OAuth2Error("invalid_token", "Token has been invalidated", null);
            return OAuth2TokenValidatorResult.failure(error);
        }
        return OAuth2TokenValidatorResult.success();
    }
}