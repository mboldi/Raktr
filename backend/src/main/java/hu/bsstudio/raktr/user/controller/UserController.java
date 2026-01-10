package hu.bsstudio.raktr.user.controller;

import hu.bsstudio.raktr.dto.user.UserDetailsDto;
import hu.bsstudio.raktr.dto.user.UserUpdateDto;
import hu.bsstudio.raktr.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Users")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDetailsDto> getUsers(@RequestParam(required = false) Boolean canIssueRent) {
        if (canIssueRent != null) {
            return userService.getUsers(canIssueRent);
        }
        return userService.getUsers();
    }

    @GetMapping("/{username}")
    public UserDetailsDto getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username);
    }

    @PutMapping("/{username}")
    public UserDetailsDto updateUser(@PathVariable String username, @RequestBody @Valid UserUpdateDto updateDto) {
        return userService.updateUser(username, updateDto);
    }

}
