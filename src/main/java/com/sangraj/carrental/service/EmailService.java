package com.sangraj.carrental.service;

import com.sangraj.carrental.config.TokenUTIL;
import com.sangraj.carrental.entity.AppUser;
import com.sangraj.carrental.entity.VarificationToken;
import com.sangraj.carrental.repository.VarificationTokenRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private final JavaMailSender javaMailSender;
  private final VarificationTokenRepository varificationTokenRepository;

  public EmailService(JavaMailSender javaMailSender,
                      VarificationTokenRepository varificationTokenRepository) {
    this.javaMailSender = javaMailSender;
    this.varificationTokenRepository = varificationTokenRepository;
  }
  @Async
  public void sendVerificationLink(AppUser user) {

    // delete old tokens by userId (IMPORTANT)
    varificationTokenRepository.deleteByUser(user);
    varificationTokenRepository.flush();

    String token = TokenUTIL.generateToken();

    VarificationToken verificationToken = new VarificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);   // store ID, not entity
    verificationToken.setExpiryDate(TokenUTIL.expiryTime());

    varificationTokenRepository.save(verificationToken);

    String link =
            "https://carrentalbackend-h8b3.onrender.com/auth/verify?token=" + token;

    SimpleMailMessage mail = new SimpleMailMessage();
    mail.setTo(user.getEmail());
    mail.setSubject("Verify your email");
    mail.setText(
            "Hi " + user.getUsername() + " 🚀\n\n" +
                    "Thanks for signing up for SAngRaj Rentals — your journey starts here! 🛣️\n\n" +
                    "Just one quick pit stop before you’re good to go 👇\n\n" +
                    "🔗 Verify your email:\n" +
                    link + "\n\n" +
                    "This link is valid for 30 minutes.\n\n" +
                    "Cheers,\n" +
                    "SAngRaj Rentals Team 🚗"
    );

    javaMailSender.send(mail);
  }
}
