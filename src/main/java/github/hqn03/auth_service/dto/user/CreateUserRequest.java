package github.hqn03.auth_service.dto.user;

public record CreateUserRequest(String username, String email, String password) {
}
