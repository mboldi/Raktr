package hu.bsstudio.raktr.controller;

import hu.bsstudio.raktr.model.User;
import hu.bsstudio.raktr.service.UserDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/user")
@SuppressWarnings("checkstyle:DesignForExtension")
public class UserDataController {

    private final UserDataService userDataService;

    public UserDataController(final UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    @GetMapping("/{username}")
    public User getUserByUsername(@PathVariable final String username) {
        log.info("Incoming request for user with name: {}", username);

        return userDataService.getByUsername(username);
    }

    @GetMapping("/canissuerent")
    public List<User> getRentIssuerableMembers() {
        log.info("incoming request for all members that can issue a rent");

        return userDataService.getRentIssuerableMembers();
    }

    @PutMapping
    public User updateUser(@RequestBody final User user) {
        log.info("Incoming request to update user: {}", user);

        return userDataService.updateUser(user);
    }
}
