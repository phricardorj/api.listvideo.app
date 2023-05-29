package br.com.phricardo.listvideo.controller.doc;

import br.com.phricardo.listvideo.dto.response.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "User", description = "Endpoints for user")
public interface UserControllerDoc {

  @GetMapping("/{username}")
  @Operation(
      summary = "Get User by Username",
      description = "Retrieves user information based on the provided username.")
  UserResponseDTO getUserByUsername(@PathVariable String username);
}
