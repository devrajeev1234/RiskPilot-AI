package com.loanguard.dto;

public record LoginResponse(
        String token,
        Long   userId,
        String fullName,
        String email,
        String role
) {}