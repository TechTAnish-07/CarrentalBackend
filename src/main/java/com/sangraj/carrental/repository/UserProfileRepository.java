package com.sangraj.carrental.repository;

import com.sangraj.carrental.entity.AppUser;
import com.sangraj.carrental.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {



   Optional<UserProfile> findByUser(AppUser user);
}
