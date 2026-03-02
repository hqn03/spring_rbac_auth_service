package github.hqn03.auth_service.repository;

import github.hqn03.auth_service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Long> {
}
