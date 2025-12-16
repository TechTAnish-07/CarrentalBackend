package com.sangraj.carrental.repository;

import com.sangraj.carrental.entity.AppUser;
import com.sangraj.carrental.entity.Car;
import com.sangraj.carrental.entity.ReviewEntity;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewSaveRepository extends JpaRepository<ReviewEntity, Long> {
    Optional<ReviewEntity> findByUsername(String Username);
}
