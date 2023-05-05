package br.com.phricardo.listvideo.service;

import static java.lang.String.format;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import br.com.phricardo.listvideo.dto.request.UserAuthLoginRequestDTO;
import br.com.phricardo.listvideo.dto.request.UserAuthRegisterRequestDTO;
import br.com.phricardo.listvideo.dto.request.mapper.UserAuthRegisterRequestMapper;
import br.com.phricardo.listvideo.dto.response.TokenResponseDTO;
import br.com.phricardo.listvideo.dto.response.UserResponseDTO;
import br.com.phricardo.listvideo.dto.response.mapper.TokenResponseMapper;
import br.com.phricardo.listvideo.dto.response.mapper.UserResponseMapper;
import br.com.phricardo.listvideo.exception.EmailNotVerifiedException;
import br.com.phricardo.listvideo.exception.LoginException;
import br.com.phricardo.listvideo.model.User;
import br.com.phricardo.listvideo.repository.UserAuthRepository;
import br.com.phricardo.listvideo.service.email.EmailSender;
import br.com.phricardo.listvideo.service.email.EmailTemplateBuilder;
import jakarta.persistence.EntityNotFoundException;
import java.util.NoSuchElementException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthenticationService implements UserDetailsService {

  private final UserAuthRepository repository;
  private final UserAuthRegisterRequestMapper registerRequestMapper;
  private final TokenResponseMapper tokenResponseMapper;
  private final UserResponseMapper userResponseMapper;
  private final TokenService tokenService;
  private final EmailSender emailSender;
  private final EmailTemplateBuilder emailTemplateBuilder;

  @Value("${app.account_activation_url}")
  private String ACCOUNT_ACTIVATION_URL;

  @Override
  public UserDetails loadUserByUsername(final String login) throws UsernameNotFoundException {
    return repository.findByEmailOrUsername(login, login);
  }

  public UserResponseDTO registerUser(
      @NonNull final UserAuthRegisterRequestDTO registerRequestDTO) {
    return of(registerRequestDTO)
        .map(registerRequestMapper::from)
        .map(repository::save)
        .map(
            user -> {
              sendAccountVerificationEmail(user);
              return user;
            })
        .map(userResponseMapper::from)
        .orElseThrow(() -> new NoSuchElementException("User registration request cannot be null"));
  }

  public TokenResponseDTO loginUser(
      @NonNull final UserAuthLoginRequestDTO loginRequestDTO, final AuthenticationManager manager) {
    final var login = loginRequestDTO.getLogin();
    final var password = loginRequestDTO.getPassword();
    final var existsByLogin = repository.existsByEmailOrUsername(login, login);

    return of(existsByLogin)
        .filter(existsUser -> existsUser)
        .map(existsUser -> new UsernamePasswordAuthenticationToken(login, password))
        .map(
            token ->
                ofNullable(manager.authenticate(token))
                    .map(
                        auth -> {
                          final var principal = auth.getPrincipal();
                          return (User) principal;
                        })
                    .map(
                        user -> {
                          if (!user.getStatus()) throw new EmailNotVerifiedException();
                          return user;
                        })
                    .map(tokenService::generate)
                    .map(tokenResponseMapper::from)
                    .orElseThrow(LoginException::new))
        .orElseThrow(LoginException::new);
  }

  public UserDetails getCurrentUserDetails() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return (UserDetails) authentication.getPrincipal();
  }

  public User getCurrentUser() {
    UserDetails userDetails = getCurrentUserDetails();
    return (User) userDetails;
  }

  public UserResponseDTO getCurrentUserDTO() {
    return userResponseMapper.from(getCurrentUser());
  }

  public User getUserByUsername(String username) {
    return repository
        .findByUsername(username)
        .orElseThrow(
            () ->
                new EntityNotFoundException(format("User with username %s not found.", username)));
  }

  public UserResponseDTO getUserResponseByUsername(String username) {
    return of(getUserByUsername(username))
        .map(userResponseMapper::from)
        .orElseThrow(
            () ->
                new EntityNotFoundException(format("User with username %s not found.", username)));
  }

  public UserResponseDTO activateAccount(String userId) {
    return repository
        .findByUserIdAndStatusFalse(userId)
        .map(
            user -> {
              user.setStatus(true);
              return user;
            })
        .map(repository::save)
        .map(userResponseMapper::from)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    format(
                        "Could not activate account: User with ID %s not found or account already activated.",
                        userId)));
  }

  private void sendAccountVerificationEmail(@NonNull User user) {
    emailSender
        .setRecipient(user.getEmail())
        .setSubject("ListVideo - Verify Your Account")
        .setBody(
            emailTemplateBuilder
                .setTemplate("email-template.html")
                .setName(user.getName())
                .setBody(
                    "Welcome to ListVideo!<br> Before you start enjoying everything we have to offer, remember to activate your account.")
                .setLinkText("Activate my account")
                .setLinkUrl(String.format("%s%s", ACCOUNT_ACTIVATION_URL, user.getUserId()))
                .build(),
            true)
        .send();
  }
}
