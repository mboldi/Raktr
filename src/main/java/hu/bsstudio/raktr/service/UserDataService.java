package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.repository.UserRepository;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.User;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
