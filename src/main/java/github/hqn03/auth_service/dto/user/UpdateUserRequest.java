package github.hqn03.auth_service.dto.user;

import java.util.Set;

public record UpdateUserRequest(
        String username,
        String email,
        Set<Long> roleIds) {
}
