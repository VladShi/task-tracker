package com.tasktracker.backend.security.service.impl;

import com.tasktracker.backend.entity.User;
import com.tasktracker.backend.exception.JwtProcessingException;
import com.tasktracker.backend.security.model.CustomUserDetails;
import com.tasktracker.backend.security.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;

@Service
public class JwtServiceImpl implements JwtService {

    public static final String USER_ID_CLAIM = "userId";
    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long expirationSeconds;

    @Autowired
    public JwtServiceImpl(JwtEncoder jwtEncoder,
                          @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") String issuer,
                          @Value("${jwt.expiration-seconds}") long expirationSeconds) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
    }

    @Override
    public String generateToken(CustomUserDetails userDetails) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(userDetails.getUsername())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(expirationSeconds))
                .claim(USER_ID_CLAIM, userDetails.getId())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public String generateToken(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user, new ArrayList<>());
        return generateToken(userDetails);
    }

    @Override
    public long extractUserId(Jwt jwt) {
        Object userIdClaim = jwt.getClaim(USER_ID_CLAIM);
        if (userIdClaim == null) {
            throw new JwtProcessingException("User ID claim is missing");
        }
        return switch (userIdClaim) {
            case Integer i -> i.longValue();
            case Long l -> l;
            default -> throw new JwtProcessingException("User ID claim must be a number");
        };
    }
}
