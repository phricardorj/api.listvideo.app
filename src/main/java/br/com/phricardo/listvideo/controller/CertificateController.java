package br.com.phricardo.listvideo.controller;

import br.com.phricardo.listvideo.dto.request.CertificateDataRequestDTO;
import br.com.phricardo.listvideo.service.CertificateService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/certificate")
@Tag(name = "Certificate", description = "Endpoints for certificates")
public class CertificateController {

  private final CertificateService certificateService;

  @GetMapping("/{courseId}")
  public ResponseEntity<Resource> getCertificateByCourseId(@PathVariable String courseId) {
    return certificateService.getCertificate(courseId);
  }

  @PostMapping
  public ResponseEntity<Resource> generateCertificate(
      @RequestBody @Valid CertificateDataRequestDTO certificateDataRequestDTO) {
    return certificateService.generateCertificate(certificateDataRequestDTO);
  }
}
