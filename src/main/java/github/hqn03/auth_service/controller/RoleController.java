package github.hqn03.auth_service.controller;

import github.hqn03.auth_service.dto.role.AssignPermissionResponse;
import github.hqn03.auth_service.dto.role.RoleRequest;
import github.hqn03.auth_service.dto.role.RoleRespone;
import github.hqn03.auth_service.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public List<RoleRespone> getAll(){
        return roleService.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoleRespone getRole(@PathVariable Long id){
        return roleService.getRole(id);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public RoleRespone createRole(@RequestBody RoleRequest roleRequest){
        return roleService.createRole(roleRequest);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RoleRespone updateRole(@PathVariable Long id, @RequestBody RoleRequest roleRequest){
        return roleService.updateRole(id, roleRequest);
    }

    @PatchMapping("/{id}/assign-permission")
    @ResponseStatus(HttpStatus.OK)
    public AssignPermissionResponse assignPermission(@PathVariable Long id, @RequestBody Set<Long> permissionIds){
        return roleService.assignPermissions(id, permissionIds);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteRole(@PathVariable Long id){
        return roleService.deleteRole(id);
    }
}
