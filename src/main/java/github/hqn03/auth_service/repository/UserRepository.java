package github.hqn03.auth_service.repository;

import github.hqn03.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername (String username);
    boolean existsByEmail (String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    @Query("""
       SELECT DISTINCT u
       FROM User u
       LEFT JOIN FETCH u.roles r
       LEFT JOIN FETCH r.permissions
      """)
    List<User> findAllIncludeRoleAndPermission();

}
