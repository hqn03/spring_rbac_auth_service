package github.hqn03.auth_service.dto.user;

import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        String email,
        Set<String> roles,
        Set<String> permissions) {
}
