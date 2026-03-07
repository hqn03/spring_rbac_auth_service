package github.hqn03.auth_service.service;

import github.hqn03.auth_service.dto.user.CreateUserRequest;
import github.hqn03.auth_service.dto.user.UpdateUserRequest;
import github.hqn03.auth_service.dto.user.UserResponse;
import github.hqn03.auth_service.exception.AppException;
import github.hqn03.auth_service.exception.ResourceNotFoundException;
import github.hqn03.auth_service.model.Permission;
import github.hqn03.auth_service.model.Role;
import github.hqn03.auth_service.model.User;
import github.hqn03.auth_service.repository.PermissionRepository;
import github.hqn03.auth_service.repository.RoleRepository;
import github.hqn03.auth_service.repository.UserRepository;
import github.hqn03.auth_service.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final SecurityService securityService;

    @Transactional
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        if(userRepository.existsByUsernameOrEmail(createUserRequest.username(),
                createUserRequest.email())){
            throw new AppException("Username or email is existed", HttpStatus.BAD_REQUEST);
        }


        String adminUsername = securityService.getUsername();
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(createUserRequest.roleIds()));
        validateRoleAssignment(roles);

        User user = new User();
        user.setUsername(createUserRequest.username());
        user.setEmail(createUserRequest.email());
        user.setPassword(passwordEncoder.encode(createUserRequest.password()));

        if(roles.isEmpty()) {
            roles.add(roleRepository.findByName("USER")
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found")));
        }

        user.setRoles(roles);
        User saved = userRepository.save(user);

        Set<String> rolesName = saved.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        log.info("Admin '{}' created user '{}' with email '{}' and roles {}",
                adminUsername, saved.getUsername(), saved.getEmail(), rolesName );

        return new UserResponse(saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                rolesName, null);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        null,
                        null))
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User user = userRepository.findWithRolePermissionById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<String> roles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        Set<String> permissions = user.getRoles()
                .stream()
                .flatMap(role -> role.getPermissions()
                        .stream())
                .map(Permission::getName)
                .collect(Collectors.toSet());
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), roles, permissions);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest updateUserRequest) {
        String adminUsername = securityService.getUsername();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (updateUserRequest.username() != null) user.setUsername(updateUserRequest.username());
        if (updateUserRequest.email() != null) user.setEmail(updateUserRequest.email());

        if (updateUserRequest.roleIds() != null && !updateUserRequest.roleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(updateUserRequest.roleIds()));
            validateRoleAssignment(roles);
            user.setRoles(roles);
        }

        User saved = userRepository.save(user);

        Set<String> rolesName = saved.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        log.info("Admin '{}' updated user '{}' with email '{}' and roles {}",
                adminUsername, saved.getUsername(), saved.getEmail(), rolesName);

        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), rolesName,null);
    }

    @Transactional
    public void deleteUser(Long id  ){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.delete(user);
    }

    private void validateRoleAssignment(Set<Role> targetRoles){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isSuperAdmin = securityService.isSuperAdmin();
        if(!isSuperAdmin){
            boolean hasHighLevelRole = targetRoles.stream()
                    .anyMatch(r -> r.getName().equals("ADMIN") || r.getName().equals("SUPER_ADMIN"));
            if(hasHighLevelRole) {
                throw new AccessDeniedException("You are not allowed to assign role Admin and Super Admin");
            }
        };
    }
}
