package github.hqn03.auth_service.dto.user;

import java.util.Set;

public record CreateUserRequest(
        String username,
        String email,
        String password,
        Set<Long> roleIds) {
}
