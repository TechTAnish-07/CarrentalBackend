package com.sangraj.carrental.controller;

import com.sangraj.carrental.entity.ContactMessage;
import com.sangraj.carrental.service.ContactService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<String> submit(@RequestBody ContactMessage msg) {
        contactService.save(msg);
        return ResponseEntity.ok("Message sent successfully");
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ContactMessage> getAll() {
        return contactService.getAll();
    }
}
