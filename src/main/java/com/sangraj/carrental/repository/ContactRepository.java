package com.sangraj.carrental.repository;

import com.sangraj.carrental.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository
        extends JpaRepository<ContactMessage, Long> {
}
