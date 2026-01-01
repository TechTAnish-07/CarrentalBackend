package com.sangraj.carrental.controller;

import com.sangraj.carrental.dto.*;
import com.sangraj.carrental.entity.AppUser;
import com.sangraj.carrental.entity.Role;
import com.sangraj.carrental.entity.VarificationToken;
import com.sangraj.carrental.repository.UserRepository;
import com.sangraj.carrental.repository.VarificationTokenRepository;
import com.sangraj.carrental.service.EmailService;
import com.sangraj.carrental.service.JwtService;
import com.sangraj.carrental.service.CustomUserDetailsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository repo;
    private final AuthenticationManager authManager;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final VarificationTokenRepository varificationTokenRepository;
    private final EmailService emailService;

    public AuthController(UserRepository repo,
                          AuthenticationManager authManager,
                          PasswordEncoder encoder,
                          JwtService jwtService,
                          CustomUserDetailsService userDetailsService ,
                          VarificationTokenRepository varificationTokenRepository,
                          EmailService emailService) {
        this.repo = repo;
        this.authManager = authManager;
        this.encoder = encoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.varificationTokenRepository = varificationTokenRepository;
        this.emailService = emailService;

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {

        Optional<AppUser> existingOpt = repo.findByEmail(req.email());

        // CASE 1: User already exists
        if (existingOpt.isPresent()) {
            AppUser existingUser = existingOpt.get();
            System.out.println("user already existed");
            // Already verified → block
            if (existingUser.isEnabled()) {
                System.out.println("is enabled");
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("Email already registered");
            }
            System.out.println("user existed but is not enabled");
            emailService.sendVerificationLink(existingUser.getId());


            return ResponseEntity.ok(
                    "Verification email resent. Please check your inbox."
            );
        }

        // CASE 2: New user
        AppUser user = new AppUser();
        user.setEmail(req.email());
        user.setDisplayName(req.username());
        user.setPassword(encoder.encode(req.password()));
        user.setRole(Role.ROLE_USER);
        user.setEnabled(false);

        user = repo.save(user); // ✅ save ONCE
        emailService.sendVerificationLink(user.getId());


        return ResponseEntity.ok(
                "Registration successful. Please verify your email."
        );
    }



    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {

        Optional<VarificationToken> optionalToken =
                varificationTokenRepository.findByToken(token);

        if (optionalToken.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or already used verification token");
        }

        VarificationToken vt = optionalToken.get();

        if (vt.getExpiryDate().isBefore(LocalDateTime.now())) {
            varificationTokenRepository.delete(vt);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Verification token expired");
        }

        AppUser user = vt.getUser();
        user.setEnabled(true);
        repo.save(user);

        varificationTokenRepository.delete(vt);

        return ResponseEntity.ok("Email verified successfully");
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletResponse response) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

       AppUser user = repo.findByEmail(req.email()).orElseThrow();
        if (!user.isEnabled()) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "message", "Please verify your email before logging in."
                    ));
        }

        String accessToken = jwtService.generateToken(
                user.getEmail(),
                user.getUsername(),
                user.getRole().name()
        );

        String refreshToken = jwtService.generateRefreshToken(user);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/auth/refresh-token");
        cookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(cookie);

        return ResponseEntity.ok(
                new AuthResponse(
                        accessToken,
                        refreshToken,
                        new UserResponse(
                                user.getId(),user.getDisplayName(),
                                user.getEmail(), user.getRole().name()
                        )
                )
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        return ResponseEntity.ok(
                Map.of("message", "Logged out successfully")
        );
    }

    // REFRESH TOKEN
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {

        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals("refreshToken")) {
                    refreshToken = c.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null)
            return ResponseEntity.status(401).body("Refresh Token Missing");

        String email = jwtService.extractEmail(refreshToken);

        UserDetails user = userDetailsService.loadUserByUsername(email);

        if (!jwtService.isRefreshTokenValid(refreshToken, user))
            return ResponseEntity.status(401).body("Invalid Refresh Token");

        AppUser app = repo.findByEmail(email).orElseThrow();

        String newAccessToken = jwtService.generateToken(
                app.getEmail(),
                app.getUsername(),
                app.getRole().name()
        );

        return ResponseEntity.ok(
                new AuthResponse(
                        newAccessToken,
                        refreshToken,
                        new UserResponse(
                                app.getId(),app.getUsername(),
                                app.getEmail(), app.getRole().name()
                        )
                )
        );
    }



}