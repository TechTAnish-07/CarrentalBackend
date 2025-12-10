package com.sangraj.carrental.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)   // disable CSRF
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // allow ALL endpoints
                )
                .formLogin(AbstractHttpConfigurer::disable)   // disable login page
                .httpBasic(basic -> basic.disable()); // disable Basic Auth

        return http.build();
    }
}
