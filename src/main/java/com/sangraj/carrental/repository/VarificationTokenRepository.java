package com.sangraj.carrental.repository;

import com.sangraj.carrental.entity.VarificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VarificationTokenRepository extends JpaRepository<VarificationToken,Long> {
    Optional<VarificationToken> findByToken(String token);
}
