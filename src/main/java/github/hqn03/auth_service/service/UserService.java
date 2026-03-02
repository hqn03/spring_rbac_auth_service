package github.hqn03.auth_service.service;

import github.hqn03.auth_service.dto.user.CreateUserRequest;
import github.hqn03.auth_service.dto.user.UpdateUserRequest;
import github.hqn03.auth_service.dto.user.UserResponse;
import github.hqn03.auth_service.model.Permission;
import github.hqn03.auth_service.model.Role;
import github.hqn03.auth_service.model.User;
import github.hqn03.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserResponse createUser(CreateUserRequest createUserRequest) {
        User user = new User();
        user.setUsername(createUserRequest.username());
        user.setEmail(createUserRequest.email());
        user.setPassword(passwordEncoder.encode(createUserRequest.password()));

        User saved = userRepository.save(user);

        return new UserResponse(saved.getId(), saved.getUsername(), saved.getEmail(),null,null);
    }

    public List<UserResponse> getUsers() {
        return userRepository.findAllIncludeRoleAndPermission()
                .stream()
                .map(user -> {
                    Set<String> roles = user.getRoles()
                            .stream()
                            .map(Role::getName)
                            .collect(Collectors.toSet());

                    Set<String> permissions = user.getRoles()
                            .stream()
                            .flatMap(role -> role.getPermissions().stream())
                            .map(Permission::getName)
                            .collect(Collectors.toSet());

                    return  new UserResponse(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            roles,
                            permissions
                    );
                })
                .toList();
    }


    public void updateUser(UpdateUserRequest updateUserRequest) {}

    public void deleteUser(String username){
    }
}
