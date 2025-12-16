package com.sangraj.carrental.dto;

public record AuthResponse(
        String token,

        String refreshToken,
       UserResponse user
) {}
