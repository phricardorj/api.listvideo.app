package br.com.phricardo.listvideo.controller;

import br.com.phricardo.listvideo.controller.doc.AuthenticationControllerDoc;
import br.com.phricardo.listvideo.dto.request.UserAuthLoginRequestDTO;
import br.com.phricardo.listvideo.dto.request.UserAuthRegisterRequestDTO;
import br.com.phricardo.listvideo.dto.response.TokenResponseDTO;
import br.com.phricardo.listvideo.dto.response.UserForgotPasswordResponseDTO;
import br.com.phricardo.listvideo.dto.response.UserResponseDTO;
import br.com.phricardo.listvideo.dto.update.UserForgotPasswordRequestDTO;
import br.com.phricardo.listvideo.service.UserAuthenticationService;
import br.com.phricardo.listvideo.service.UserForgotPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v2/auth")
public class AuthenticationController implements AuthenticationControllerDoc {

  private final UserAuthenticationService service;
  private final UserForgotPasswordService userForgotPasswordService;
  private final AuthenticationManager authenticationManager;

  @PostMapping("/register")
  public UserResponseDTO register(
      @RequestBody @Valid UserAuthRegisterRequestDTO registerRequestDTO) {
    return service.registerUser(registerRequestDTO);
  }

  @PostMapping("/login")
  public TokenResponseDTO login(@RequestBody @Valid UserAuthLoginRequestDTO loginRequestDTO) {
    return service.loginUser(loginRequestDTO, authenticationManager);
  }

  @PostMapping("/forgot-password")
  public void sendPasswordResetLink(@RequestParam("email") String email) {
    userForgotPasswordService.sendPasswordResetLink(email);
  }

  @PostMapping("/reset-password")
  public UserForgotPasswordResponseDTO resetUserPassword(
      @RequestBody @Valid UserForgotPasswordRequestDTO userForgotPasswordRequestDTO) {
    return userForgotPasswordService.resetUserPassword(userForgotPasswordRequestDTO);
  }

  @PatchMapping("/activate-account/{userId}")
  public UserResponseDTO activeAccount(@PathVariable String userId) {
    return service.activateAccount(userId);
  }

  @PostMapping("/account/activation-resend")
  public void accountActivationEmailResend(@RequestParam("email") String email) {
    service.accountActivationEmailResend(email);
  }
}
