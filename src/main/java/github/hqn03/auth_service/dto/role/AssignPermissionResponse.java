package github.hqn03.auth_service.dto.role;

import java.util.Set;

public record AssignPermissionResponse(Long id, String name, String description, Set<String> permissions) {
}
