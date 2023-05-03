package br.com.phricardo.listvideo.repository;

import br.com.phricardo.listvideo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAuthRepository extends JpaRepository<User, UUID> {

    UserDetails findByEmailOrUsername(String email, String username);

    boolean existsByEmailOrUsername(String email, String username);

    Optional<User> findByIdAndStatusFalse(UUID id);

    Optional<User> findByUsername(String username);
}
