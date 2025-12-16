package com.sangraj.carrental.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ContactMessage {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String email;
    private String subject;

    @Column(length = 1000)
    private String message;

    private LocalDateTime createdAt;
}
