package com.bezkoder.spring.jwt.mongodb.repository;

import java.util.Optional;

import com.bezkoder.spring.jwt.mongodb.models.RefreshToken;
import com.bezkoder.spring.jwt.mongodb.models.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}