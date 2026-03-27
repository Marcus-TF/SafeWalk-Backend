package com.safewalk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "NOTIFY_HIGH", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean notifyHigh;

    @Column(name = "NOTIFY_MEDIUM", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean notifyMedium;

    @Column(name = "NOTIFY_LOW", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean notifyLow;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Occurrence> occurrences;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (notifyHigh == null) notifyHigh = false;
        if (notifyMedium == null) notifyMedium = false;
        if (notifyLow == null) notifyLow = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
