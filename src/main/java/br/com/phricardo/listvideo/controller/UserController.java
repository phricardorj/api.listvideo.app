package br.com.phricardo.listvideo.controller;

import br.com.phricardo.listvideo.dto.response.UserResponseDTO;
import br.com.phricardo.listvideo.service.UserAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v2/user")
@Tag(name = "User", description = "Endpoints for user")
public class UserController {

  private final UserAuthenticationService userAuthenticationService;

  @GetMapping("/{username}")
  @Operation(
      summary = "Get User by Username",
      description = "Retrieves user information based on the provided username.")
  public UserResponseDTO getUserByUsername(@PathVariable String username) {
    return userAuthenticationService.getUserResponseByUsername(username);
  }

  @GetMapping("/authenticated")
  @SecurityRequirement(name = "bearer-key")
  @Operation(
      summary = "Get Current Authenticated User",
      description = "Retrieves information about the currently authenticated user.")
  public UserResponseDTO getCurrentAuthenticatedUser() {
    return userAuthenticationService.getCurrentUserDTO();
  }
}
