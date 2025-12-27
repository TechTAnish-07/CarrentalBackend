package com.sangraj.carrental.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Table(name = "VarificationToken")
@Data
public class VarificationToken  {
    @Id
    @GeneratedValue
    Integer Id;
    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;


}
