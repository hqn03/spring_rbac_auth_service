package github.hqn03.auth_service.service;

import github.hqn03.auth_service.dto.role.AssignPermissionResponse;
import github.hqn03.auth_service.dto.role.RoleRequest;
import github.hqn03.auth_service.dto.role.RoleRespone;
import github.hqn03.auth_service.exception.ResourceNotFoundException;
import github.hqn03.auth_service.model.Permission;
import github.hqn03.auth_service.model.Role;
import github.hqn03.auth_service.repository.PermissionRepository;
import github.hqn03.auth_service.repository.RoleRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public List<RoleRespone> getAll(){
        return roleRepository.findAll()
                .stream()
                .map(role -> new RoleRespone(role.getId(), role.getName(), role.getDescription()))
                .toList();
    }

    public RoleRespone getRole(Long id){
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));

        return new RoleRespone(role.getId(), role.getName(), role.getDescription());
    }

    public RoleRespone createRole(RoleRequest roleRequest){
        Role role = new Role();

        role.setName(roleRequest.name());
        role.setDescription(roleRequest.decription());

        roleRepository.save(role);
        return new RoleRespone(role.getId(), role.getName(), role.getDescription());
    }

    public RoleRespone updateRole(Long id,  RoleRequest roleRequest){
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));

        role.setName(roleRequest.name());
        role.setDescription(roleRequest.decription());

        return new RoleRespone(role.getId(), role.getName(), role.getDescription());
    }

    public String deleteRole(Long id){
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));

        roleRepository.delete(role);
        return "Role Deleted Successfully";
    }

    public AssignPermissionResponse assignPermissions(Long roleId, Set<Long> permissionIds){
        String adminName = SecurityContextHolder.getContext().getAuthentication().getName();

        Role role =  roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role Not Found"));

        if (role.getName().equals("SUPER_ADMIN")) {
            throw new AccessDeniedException("Can not change permission Super Admin Role");
        }

        Set<Permission> permissions = new HashSet<>(permissionRepository.findAllById(permissionIds));
        if (permissions.size() != permissionIds.size()) {
            throw new ResourceNotFoundException("Some permissions not found");
        }

        role.setPermissions(permissions);
        Role savedRole = roleRepository.save(role);

        Set<String> permissionNames = savedRole.getPermissions()
                .stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());

        log.info("Admin [{}] changed permissons of Role [{}]. Permissions: [{}]", adminName, role.getName() , permissionNames);

        return new AssignPermissionResponse(role.getId(), role.getName(), role.getDescription(), permissionNames);
    }
}
