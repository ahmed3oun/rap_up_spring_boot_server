package com.bezkoder.spring.jwt.mongodb.security.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.bezkoder.spring.jwt.mongodb.Exceptions.TokenRefreshException;
import com.bezkoder.spring.jwt.mongodb.models.RefreshToken;
import com.bezkoder.spring.jwt.mongodb.repository.RefreshTokenRepository;
import com.bezkoder.spring.jwt.mongodb.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${bezkoder.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken creatRefreshToken(String userId) {

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(),
                    "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    public void deleteByUserId(String id) {
        refreshTokenRepository.deleteByUser(userRepository.findById(id).get());
    }
}