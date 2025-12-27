package com.sangraj.carrental.service;

import com.sangraj.carrental.config.TokenUTIL;
import com.sangraj.carrental.entity.AppUser;
import com.sangraj.carrental.entity.VarificationToken;
import com.sangraj.carrental.repository.UserRepository;
import com.sangraj.carrental.repository.VarificationTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class BrevoEmailService implements EmailService {

    @Value("${BREVO_API_KEY}")
    private String apiKey;

    @Value("${app.backend-url}")
    private String backendUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final UserRepository userRepo;
    private final VarificationTokenRepository tokenRepo;

    public BrevoEmailService(UserRepository userRepo,
                             VarificationTokenRepository tokenRepo) {
        this.userRepo = userRepo;
        this.tokenRepo = tokenRepo;
    }

    @Override
    @Async
    public void sendVerificationLink(Long userId) {

        AppUser user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        tokenRepo.deleteByUser(user);

        String token = TokenUTIL.generateToken();

        VarificationToken vt = new VarificationToken();
        vt.setToken(token);
        vt.setUser(user);
        vt.setExpiryDate(TokenUTIL.expiryTime());
        tokenRepo.save(vt);

        String link = backendUrl + "/auth/verify?token=" + token;

        sendViaBrevo(user.getEmail(), user.getUsername(), link);
    }

    private void sendViaBrevo(String toEmail, String username, String link) {

        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        String body = """
        {
          "sender": {
            "name": "SAngRaj Rentals",
            "email": "no-reply@smtp-brevo.com"
          },
          "to": [{
            "email": "%s",
            "name": "%s"
          }],
          "subject": "Verify your email – SAngRaj Rentals 🚗",
          "htmlContent": "<p>Hi %s 👋</p>
                          <p>Welcome to <b>SAngRaj Rentals</b>.</p>
                          <p>Click below to verify your email:</p>
                          <p><a href='%s'>Verify Email</a></p>
                          <p>This link expires in 30 minutes.</p>"
        }
        """.formatted(toEmail, username, username, link);

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, request, String.class);

        System.out.println("✅ Brevo email sent to " + toEmail);
    }
}
