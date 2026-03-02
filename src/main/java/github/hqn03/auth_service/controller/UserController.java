package github.hqn03.auth_service.controller;

import github.hqn03.auth_service.dto.user.CreateUserRequest;
import github.hqn03.auth_service.dto.user.UserResponse;
import github.hqn03.auth_service.model.User;
import github.hqn03.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@RequestBody CreateUserRequest createUserRequest) {
        return userService.createUser(createUserRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }


}
