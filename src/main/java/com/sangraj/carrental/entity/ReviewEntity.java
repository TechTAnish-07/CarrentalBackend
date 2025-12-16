package com.sangraj.carrental.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "UserReview")
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String comment;
    String username;
    private int rating;

    private LocalDateTime createdAt;
}
