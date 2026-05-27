package com.safewalk.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_activation_token")
@Data
public class EmailActivationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private LocalDateTime expiresAt;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
