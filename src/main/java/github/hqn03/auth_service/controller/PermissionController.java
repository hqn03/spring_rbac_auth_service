package github.hqn03.auth_service.controller;

import github.hqn03.auth_service.constant.PermissionConstant;
import github.hqn03.auth_service.model.Permission;
import github.hqn03.auth_service.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Permission> getAll(){
        return permissionService.getAll();
    }
}
