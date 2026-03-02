package github.hqn03.auth_service.repository;

import github.hqn03.auth_service.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission,Long> {
}
