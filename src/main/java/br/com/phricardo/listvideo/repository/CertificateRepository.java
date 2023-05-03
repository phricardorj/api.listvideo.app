package br.com.phricardo.listvideo.repository;

import br.com.phricardo.listvideo.model.Certificate;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, UUID> {

  @Query(
      "SELECT COUNT(c) > 0 FROM Certificate c WHERE c.user.id = :userId AND c.courseId = :courseId")
  boolean existsByUserIdAndCourseId(
      @Param("userId") UUID userId, @Param("courseId") String courseId);

  Certificate findByUserIdAndCourseId(UUID userId, String courseId);
}
