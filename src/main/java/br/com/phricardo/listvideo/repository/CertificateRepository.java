package br.com.phricardo.listvideo.repository;

import br.com.phricardo.listvideo.model.Certificate;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, UUID> {

  Certificate findByUserIdAndCourseId(Long userId, String courseId);
}
