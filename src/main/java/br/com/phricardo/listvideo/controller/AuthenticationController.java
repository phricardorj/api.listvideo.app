package br.com.phricardo.listvideo.controller;

import br.com.phricardo.listvideo.controller.doc.AuthenticationControllerDoc;
import br.com.phricardo.listvideo.dto.request.UserAuthLoginRequestDTO;
import br.com.phricardo.listvideo.dto.request.UserAuthRegisterRequestDTO;
import br.com.phricardo.listvideo.dto.response.TokenResponseDTO;
import br.com.phricardo.listvideo.dto.response.UserResponseDTO;
import br.com.phricardo.listvideo.service.UserAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v2/auth")
public class AuthenticationController implements AuthenticationControllerDoc {

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

  @GetMapping("/authenticated/user")
  public UserResponseDTO getCurrentAuthenticatedUser() {
    return service.getCurrentUserDTO();
  }
}
