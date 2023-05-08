package br.com.phricardo.listvideo.controller;

import br.com.phricardo.listvideo.dto.request.UserAuthLoginRequestDTO;
import br.com.phricardo.listvideo.dto.request.UserAuthRegisterRequestDTO;
import br.com.phricardo.listvideo.dto.request.UserForgotPasswordRequestDTO;
import br.com.phricardo.listvideo.dto.response.TokenResponseDTO;
import br.com.phricardo.listvideo.dto.response.UserResponseDTO;
import br.com.phricardo.listvideo.service.UserAuthenticationService;
import br.com.phricardo.listvideo.service.UserForgotPasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v2/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthenticationController {

  private final UserAuthenticationService service;
  private final UserForgotPasswordService userForgotPasswordService;
  private final AuthenticationManager authenticationManager;

  @PostMapping("/register")
  @Operation(summary = "User Registration", description = "Registers a new user.")
  public UserResponseDTO register(
      @RequestBody @Valid UserAuthRegisterRequestDTO registerRequestDTO) {
    return service.registerUser(registerRequestDTO);
  }

  @PostMapping("/login")
  @Operation(
      summary = "User Login",
      description = "Authenticates a user and returns a JWT token if the credentials are valid.")
  public TokenResponseDTO login(@RequestBody @Valid UserAuthLoginRequestDTO loginRequestDTO) {
    return service.loginUser(loginRequestDTO, authenticationManager);
  }

  @PatchMapping("/activate-account/{userId}")
  @Operation(
      summary = "Activate User Account",
      description = "Activates a user's account based on the provided user ID.")
  public UserResponseDTO activeAccount(@PathVariable String userId) {
    return service.activateAccount(userId);
  }

  @PostMapping("/forgot-password")
  @Operation(
      summary = "Send Password Reset Token",
      description =
          "Generates a password reset token and sends it to the user's email if they are registered.")
  public void sendPasswordResetLink(@RequestParam("email") String email) {
    userForgotPasswordService.sendPasswordResetLink(email);
  }

  @PostMapping("/reset-password")
  @Operation(
      summary = "Reset User Password if Token is Valid",
      description =
          "Resets the user's password in the application based on the validity of the token.")
  public String resetUserPassword(
      @RequestBody @Valid UserForgotPasswordRequestDTO userForgotPasswordRequestDTO) {
    return userForgotPasswordService.resetUserPassword(userForgotPasswordRequestDTO);
  }
}
