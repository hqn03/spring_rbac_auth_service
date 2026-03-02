package github.hqn03.auth_service.repository;

import github.hqn03.auth_service.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
}
