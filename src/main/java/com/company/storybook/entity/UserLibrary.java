package com.company.storybook.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_library")
@Data
public class UserLibrary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storybook_id", nullable = false)
    @ToString.Exclude
    private Storybook storybook;

    @Column(name = "purchased_at", updatable = false)
    private LocalDateTime purchasedAt = LocalDateTime.now();
}
