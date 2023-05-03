package br.com.phricardo.listvideo.controller;

import br.com.phricardo.listvideo.dto.request.UserAuthLoginRequestDTO;
import br.com.phricardo.listvideo.dto.request.UserAuthRegisterRequestDTO;
import br.com.phricardo.listvideo.dto.response.TokenResponseDTO;
import br.com.phricardo.listvideo.dto.response.UserResponseDTO;
import br.com.phricardo.listvideo.service.UserAuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user authentication and registration")
public class AuthenticationController {

  private final UserAuthenticationService service;
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

  @PatchMapping("/activate-account/{resourceId}")
  public UserResponseDTO activeAccount(@PathVariable String resourceId) {
    return service.activateAccount(resourceId);
  }
}
