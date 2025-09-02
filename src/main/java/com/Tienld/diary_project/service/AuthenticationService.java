package com.Tienld.diary_project.service;

import com.Tienld.diary_project.dto.request.LoginRequest;
import com.Tienld.diary_project.dto.response.AuthenticationResponse;
import com.Tienld.diary_project.entity.InvalidatedToken;
import com.Tienld.diary_project.entity.UserEntity;
import com.Tienld.diary_project.exception.ApplicationException;
import com.Tienld.diary_project.exception.ErrorCode;
import com.Tienld.diary_project.repository.InvalidateTokenRepository;
import com.Tienld.diary_project.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InvalidateTokenRepository invalidateTokenRepository;
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String Signer_Key;
    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long Valid_Duration;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long Refresh_Duration;

    public AuthenticationResponse authenticate(LoginRequest loginRequest) {
        var user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(loginRequest.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }
        String token = genToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    private String genToken(UserEntity user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("Tienld.com")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + Valid_Duration * 1000L)) // 1 ngày
                .jwtID(UUID.randomUUID().toString())
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(Signer_Key.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot sign JWS object", e);
            throw new RuntimeException(e);
        }
    }

    private SignedJWT verifyToken(String token, Boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(Signer_Key.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
                .plus(Refresh_Duration, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();
//        Date expiryTime;
//        if (isRefresh) {  // Nếu là Refresh Token
//            expiryTime = new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant()
//                    .plus(Refresh_Duration, ChronoUnit.SECONDS).toEpochMilli());
//        } else {  // Access Token
//            expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
//        }
        boolean verified = signedJWT.verify(verifier); // check chữ ký
        if (!(verified && expiryTime.after(new Date()))) {
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }

        if (invalidateTokenRepository.existsByJti(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }
        return signedJWT;
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }
        String token = authHeader.substring(7);
        try {
            SignedJWT signedJWT = verifyToken(token, false);
            String jti = signedJWT.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .jti(jti)
                    .expiryTime(expiryTime)
                    .build();
            invalidateTokenRepository.save(invalidatedToken);
            log.info("Token with jti={} invalidated until {}", jti, expiryTime);
        } catch (ParseException | JOSEException e) {
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }

    }


}
