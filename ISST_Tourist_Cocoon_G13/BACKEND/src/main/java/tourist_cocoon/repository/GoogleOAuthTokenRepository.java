package tourist_cocoon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tourist_cocoon.model.GoogleOAuthToken;

public interface GoogleOAuthTokenRepository extends JpaRepository<GoogleOAuthToken, Long> {
    Optional<GoogleOAuthToken> findByUsuarioId(Long userId);
}

