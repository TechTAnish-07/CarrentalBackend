package com.sangraj.carrental.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtFilter;
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("http://localhost:*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(cs -> cs.disable())
                .authorizeHttpRequests(auth -> auth
                        // Allow preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // VERY IMPORTANT
                        .requestMatchers("/error").permitAll()

                        // Auth endpoints
                        .requestMatchers(
                                "/auth/register",
                                "/auth/login",
                                "/auth/logout",
                                "/auth/refresh-token",
                                "/auth/verify",
                                "/auth/user/delete",
                                "/auth/user-KYC/upload",
                                "/auth/user-details",
                                "/auth/test-mail"
                        ).permitAll()

                        // Public APIs

                        .requestMatchers("/api/cars/display/available").permitAll()
                        .requestMatchers("/api/user/reviews").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/reviews").hasRole("USER")
                        .requestMatchers("/api/contact").permitAll()
                        .requestMatchers("/api/user/booking/**").hasRole("USER")
                        .requestMatchers("/api/admin/bookings/**").hasRole("ADMIN")
                        .requestMatchers("/api/cars/display").permitAll()
                        .requestMatchers("/api/user-detail/**").permitAll()
                        .requestMatchers("/api/car-inspection/**").permitAll()
                        // Admin-only
                        .requestMatchers("/api/cars/all").hasRole("ADMIN")
                        .requestMatchers("/api/cars/add").hasRole("ADMIN")

                        // Everything else

                        .anyRequest().authenticated()
                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    // keep the AuthenticationManager bean if you need it in controllers
    @Bean
    public AuthenticationManager authenticationManager(org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
