package github.hqn03.auth_service.dto.user;

import github.hqn03.auth_service.model.Permission;
import github.hqn03.auth_service.model.Role;

import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        String email,
        Set<String> roles,
        Set<String> permissions) {
}
