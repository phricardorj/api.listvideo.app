package br.com.phricardo.listvideo.service;

import br.com.phricardo.listvideo.dto.request.CertificateDataRequestDTO;
import br.com.phricardo.listvideo.dto.request.mapper.CertificateDataRequestMapper;
import br.com.phricardo.listvideo.model.Certificate;
import br.com.phricardo.listvideo.repository.CertificateRepository;
import br.com.phricardo.listvideo.service.certificate.CertificateResourceBuilder;
import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateResourceBuilder certificateResourceBuilder;
    private final CertificateRepository certificateRepository;
    private final CertificateDataRequestMapper certificateDataRequestMapper;
    private final UserAuthenticationService userAuthenticationService;

    public ResponseEntity<Resource> generateCertificate(@NonNull final CertificateDataRequestDTO certificateDataRequestDTO) {
        final var user = userAuthenticationService.getCurrentUser();
        final var new_certificate = certificateDataRequestMapper.from(certificateDataRequestDTO, user);
        Certificate certificate = certificateRepository.findByUserIdAndCourseId(user.getId(), new_certificate.getCourseId());

        if (isNull(certificate))
            certificate = certificateRepository.save(new_certificate);

        return certificateResourceBuilder.buildCertificateResource(certificate);
    }

    public ResponseEntity<Resource> getCertificate(String courseId) {
        final var user = userAuthenticationService.getCurrentUser();
        Certificate certificate = certificateRepository.findByUserIdAndCourseId(user.getId(), courseId);

        if (isNull(certificate))
            throw new EntityNotFoundException("User does not have a certificate with this course id: " + courseId);

        return certificateResourceBuilder.buildCertificateResource(certificate);
    }
}