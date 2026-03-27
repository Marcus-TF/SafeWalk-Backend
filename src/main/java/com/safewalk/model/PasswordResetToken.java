package com.safewalk.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_token")
@Data
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private LocalDateTime expiresAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
