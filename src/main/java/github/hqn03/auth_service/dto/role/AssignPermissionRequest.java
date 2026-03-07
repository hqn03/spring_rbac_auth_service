package github.hqn03.auth_service.dto.role;

import java.util.Set;

public record AssignPermissionRequest(
        Long roleId,
        Set<Long> permissionIds
){}
