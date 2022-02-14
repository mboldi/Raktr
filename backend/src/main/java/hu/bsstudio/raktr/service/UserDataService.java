package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.User;
import hu.bsstudio.raktr.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserDataService {

    private final UserRepository userRepository;

    public UserDataService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public final User getByUsername(final String username) {
        Optional<User> foundUser = userRepository.findByUsername(username);

        if (foundUser.isEmpty()) {
            log.error("User by username {} not found", username);
            throw new ObjectNotFoundException();
        } else {
            log.info("Found user: {}", foundUser.get());
            return foundUser.get();
        }
    }

    public final User updateUser(final User user) {
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());

        if (foundUser.isEmpty()) {
            log.error("User {} not found", user);
            throw new ObjectNotFoundException();
        } else {
            User updatedUser = foundUser.get();
            updatedUser.setNickName(user.getNickName());
            updatedUser.setPersonalId(user.getPersonalId());

            log.info("Updated user: {}", updatedUser);
            return userRepository.save(updatedUser);
        }
    }

    public List<User> getRentIssuerableMembers() {
        List<User> allUsers = userRepository.findAll();

        List<User> fullAccessMembers = allUsers.stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(userRole -> Objects.equals(userRole.getRoleName(), "ROLE_Stúdiós")
                                || Objects.equals(userRole.getRoleName(), "ROLE_Stúdiós-jelölt")))
                .collect(Collectors.toList());

        log.info("Found full access members: {}", fullAccessMembers);

        return fullAccessMembers;
    }
}
