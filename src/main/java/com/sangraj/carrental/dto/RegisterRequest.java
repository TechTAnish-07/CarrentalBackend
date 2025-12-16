package com.sangraj.carrental.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterRequest(
        @JsonProperty("email") String email,
        @JsonProperty("username") String username,
        @JsonProperty("password") String password
) {

}
