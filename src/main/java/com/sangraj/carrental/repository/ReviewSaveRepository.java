package com.sangraj.carrental.repository;

import com.sangraj.carrental.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewSaveRepository extends JpaRepository<ReviewEntity, Long> {
    Optional<ReviewEntity> findByUsername(String Username);
}
