package com.safewalk.repository;

import com.safewalk.model.EmailActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailActivationTokenRepository extends JpaRepository<EmailActivationToken, Long> {

    Optional<EmailActivationToken> findByToken(String token);

}
