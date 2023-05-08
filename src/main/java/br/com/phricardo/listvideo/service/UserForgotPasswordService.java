package br.com.phricardo.listvideo.service;

import br.com.phricardo.listvideo.dto.request.UserForgotPasswordRequestDTO;
import br.com.phricardo.listvideo.model.User;
import br.com.phricardo.listvideo.model.UserPasswordResetToken;
import br.com.phricardo.listvideo.repository.UserAuthRepository;
import br.com.phricardo.listvideo.repository.UserPasswordResetTokenRepository;
import br.com.phricardo.listvideo.service.email.EmailSender;
import br.com.phricardo.listvideo.service.email.EmailTemplateBuilder;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserForgotPasswordService {

  private static final int EXPIRATION = 60 * 24;

  private final EmailSender emailSender;
  private final EmailTemplateBuilder emailTemplateBuilder;
  private final UserPasswordResetTokenRepository userPasswordResetTokenRepository;
  private final UserAuthRepository userAuthRepository;
  private final PasswordEncoder passwordEncoder;

  @Value("${app.password_recovery_url}")
  private String PASSWORD_RECOVERY_URL;

  public void sendPasswordResetLink(String email) {
    final var user = findUserByEmail(email);
    final var token = generateToken(user);
    final var emailBody = buildEmailBody(user.getName(), token);
    sendPasswordResetEmail(user.getEmail(), emailBody);
  }

  public String resetUserPassword(UserForgotPasswordRequestDTO userForgotPasswordRequestDTO) {
    final var token = userForgotPasswordRequestDTO.getToken();
    final var isTokenValid = isTokenValid(token);

    if (isTokenValid) {
      final var newPassword = userForgotPasswordRequestDTO.getNewPassword();
      final var userPasswordResetToken = findUserPasswordResetTokenByToken(token);
      final var user = userPasswordResetToken.getUser();
      user.setPassword(passwordEncoder.encode(newPassword));
      userAuthRepository.save(user);
      return "Password changed successfully!";
    }
    return "Invalid or expired password reset token";
  }

  public boolean isTokenValid(String token) {
    final var userPasswordResetToken = findUserPasswordResetTokenByToken(token);
    final var now = LocalDateTime.now();
    final var expiryDate = userPasswordResetToken.getExpiryDate();
    return now.isBefore(expiryDate);
  }

  private UserPasswordResetToken findUserPasswordResetTokenByToken(String token) {
    return userPasswordResetTokenRepository
        .findByToken(token)
        .orElseThrow(() -> new NoSuchElementException("There is no password reset token"));
  }

  private User findUserByEmail(String email) {
    return userAuthRepository
        .findByEmail(email)
        .orElseThrow(() -> new NoSuchElementException("No user found with email: " + email));
  }

  private String generateToken(User user) {
    byte[] tokenBytes = generateRandomTokenBytes();
    String token = encodeToken(tokenBytes);
    LocalDateTime expiryDate = calculateExpiryDate();
    savePasswordResetToken(token, expiryDate, user);
    return token;
  }

  private byte[] generateRandomTokenBytes() {
    SecureRandom secureRandom = new SecureRandom();
    byte[] tokenBytes = new byte[32];
    secureRandom.nextBytes(tokenBytes);
    return tokenBytes;
  }

  private String encodeToken(byte[] tokenBytes) {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
  }

  private LocalDateTime calculateExpiryDate() {
    return LocalDateTime.now().plusMinutes(EXPIRATION);
  }

  private void savePasswordResetToken(String token, LocalDateTime expiryDate, User user) {
    UserPasswordResetToken passwordResetToken =
        UserPasswordResetToken.builder().token(token).expiryDate(expiryDate).user(user).build();
    userPasswordResetTokenRepository.save(passwordResetToken);
  }

  private String buildEmailBody(String userName, String token) {
    String resetLink = PASSWORD_RECOVERY_URL + token;

    return emailTemplateBuilder
        .setTemplate("email-template.html")
        .setName(userName)
        .setBody(getEmailBodyContent())
        .setLinkText("Recover Password")
        .setLinkUrl(resetLink)
        .build();
  }

  private String getEmailBodyContent() {
    return "We received a request to reset the password for your account. To proceed with the password reset, please click on the link below.<br>"
        + "If you did not request a password reset, you can safely ignore this email. Your account remains secure.";
  }

  private void sendPasswordResetEmail(String recipient, String emailBody) {
    emailSender
        .setRecipient(recipient)
        .setSubject("ListVideo - Password recovery")
        .setBody(emailBody, true)
        .send();
  }
}