package br.com.phricardo.listvideo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "User Response")
public class UserResponseDTO {

  private String name;
  private String email;
  private String username;
  private Boolean status;
  private Boolean isVerifiedAccount;
}
