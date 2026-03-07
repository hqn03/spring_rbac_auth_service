package github.hqn03.auth_service.repository;

import github.hqn03.auth_service.model.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername (String username);
    boolean existsByEmail (String email);
    boolean existsByUsernameOrEmail(String username, String email);

    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByUsername (String username);
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<User> findWithRolePermissionById(Long id);
}
