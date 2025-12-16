package com.sangraj.carrental.service;

import com.sangraj.carrental.entity.ContactMessage;
import com.sangraj.carrental.repository.ContactRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactService {

    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public ContactMessage save(ContactMessage msg) {
        msg.setCreatedAt(LocalDateTime.now());
        return contactRepository.save(msg);
    }

    public List<ContactMessage> getAll() {
        return contactRepository.findAll();
    }
}
