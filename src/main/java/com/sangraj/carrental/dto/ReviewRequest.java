package com.sangraj.carrental.dto;

public record ReviewRequest(
        String name,
        int rating,
        String comment
) {}
