package com.sangraj.carrental.service;

import com.sangraj.carrental.config.TokenUTIL;
import com.sangraj.carrental.entity.AppUser;
import com.sangraj.carrental.entity.VarificationToken;
import com.sangraj.carrental.repository.UserRepository;
import com.sangraj.carrental.repository.VarificationTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private final JavaMailSender mailSender;
  private final VarificationTokenRepository tokenRepo;
  private final UserRepository userRepo;

  @Value("${app.backend-url}")
  private String backendUrl;

  public EmailService(JavaMailSender mailSender,
                      VarificationTokenRepository tokenRepo,
                      UserRepository userRepo) {
    this.mailSender = mailSender;
    this.tokenRepo = tokenRepo;
    this.userRepo = userRepo;
  }

  @Async
  public void sendVerificationLink(Long userId) {

    try {
      AppUser user = userRepo.findById(userId)
              .orElseThrow(() -> new RuntimeException("User not found"));

      // delete old token
      tokenRepo.deleteByUser(user);

      String token = TokenUTIL.generateToken();

      VarificationToken vt = new VarificationToken();
      vt.setToken(token);
      vt.setUser(user);
      vt.setExpiryDate(TokenUTIL.expiryTime());

      tokenRepo.save(vt);

      String link = backendUrl + "/auth/verify?token=" + token;

      SimpleMailMessage mail = new SimpleMailMessage();
      mail.setTo(user.getEmail());
      mail.setFrom("patidartanish31@gmail.com");
      mail.setSubject("Verify your email – SAngRaj Rentals 🚗");
      mail.setText(
              "Hi " + user.getUsername() + " 👋\n\n" +
                      "Welcome to SAngRaj Rentals!\n\n" +
                      "Please verify your email by clicking the link below:\n\n" +
                      link + "\n\n" +
                      "⏰ This link is valid for 30 minutes.\n\n" +
                      "If you didn’t sign up, ignore this email.\n\n" +
                      "— Team SAngRaj Rentals 🚗"
      );

      mailSender.send(mail);
      System.out.println("✅ Verification email sent to " + user.getEmail());

    } catch (Exception e) {
      System.err.println("❌ EMAIL FAILED");
      e.printStackTrace();
    }
  }
}
