package br.com.phricardo.listvideo.model;

import static jakarta.persistence.GenerationType.IDENTITY;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity(name = "UserPasswordResetToken")
@Table(name = "users_password_reset_token")
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class UserPasswordResetToken {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  private String token;

  @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
  @JoinColumn(name = "username", referencedColumnName = "username", nullable = false)
  private User user;

  private LocalDateTime expiryDate;
}
