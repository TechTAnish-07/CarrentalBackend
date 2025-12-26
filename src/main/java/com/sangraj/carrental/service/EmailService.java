package com.sangraj.carrental.service;

import com.sangraj.carrental.config.TokenUTIL;
import com.sangraj.carrental.entity.AppUser;
import com.sangraj.carrental.entity.VarificationToken;
import com.sangraj.carrental.repository.VarificationTokenRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
  public final JavaMailSender javaMailSender;
  public final VarificationTokenRepository varificationTokenRepository;
  public EmailService(JavaMailSender javaMailSender , VarificationTokenRepository varificationTokenRepository){
    this.javaMailSender = javaMailSender;
    this.varificationTokenRepository = varificationTokenRepository;
  }
  public void sendVerificationLink(AppUser user) {
    varificationTokenRepository.deleteByUser(user);
    varificationTokenRepository.flush();
    String token = TokenUTIL.generateToken();

    VarificationToken verificationToken = new VarificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);
    verificationToken.setExpiryDate(TokenUTIL.expiryTime());

    varificationTokenRepository.save(verificationToken);
    System.out.println("token generated is ");
    System.out.println(token);
    String link = "http://localhost:8080/auth/verify?token=" + token;

    SimpleMailMessage mail = new SimpleMailMessage();
    mail.setTo(user.getEmail());
    mail.setSubject("Verify your email");
    mail.setText(
            "Hello " + user.getUsername() + ",\n\n" +
                    "Please verify your email by clicking the link below:\n" +
                    link + "\n\n" +
                    "This link will expire in 30 minutes."
    );

    javaMailSender.send(mail);

  }
}
