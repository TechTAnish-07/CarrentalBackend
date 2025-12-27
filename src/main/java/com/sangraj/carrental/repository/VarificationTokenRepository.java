package com.sangraj.carrental.repository;

import com.sangraj.carrental.entity.AppUser;
import com.sangraj.carrental.entity.VarificationToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VarificationTokenRepository extends JpaRepository<VarificationToken,Long> {
    Optional<VarificationToken> findByToken(String token);
    Optional<VarificationToken> findByUser(AppUser user);
    @Transactional
    void deleteByUser(AppUser user);


}
