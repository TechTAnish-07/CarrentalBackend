package com.sangraj.carrental.dto;

public record UserResponse(
        Long id,
        String displayname,
        String email,
        String role
) {}
