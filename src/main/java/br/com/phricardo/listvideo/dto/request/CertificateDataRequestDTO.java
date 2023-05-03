package br.com.phricardo.listvideo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "Certificate Data Request")
public class CertificateDataRequestDTO {

  @NotBlank(message = "courseId is required")
  private String courseId;

  @NotBlank(message = "duration is required")
  private String duration;
}
