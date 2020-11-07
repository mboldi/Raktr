package hu.bsstudio.raktr.service;

import hu.bsstudio.raktr.dao.UserDao;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.model.User;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserDataService {

    private final UserDao userDao;

    public UserDataService(final UserDao userDao) {
        this.userDao = userDao;
    }

    public final User getByUsername(final String username) {
        Optional<User> foundUser = userDao.findByUsername(username);

        if (foundUser.isEmpty()) {
            log.error("User by username {} not found", username);
            throw new ObjectNotFoundException();
        } else {
            log.info("Found user: {}", foundUser.get());
            return foundUser.get();
        }
    }

    public final User updateUser(final User user) {
        Optional<User> foundUser = userDao.findByUsername(user.getUsername());

        if (foundUser.isEmpty()) {
            log.error("User {} not found", user);
            throw new ObjectNotFoundException();
        } else {
            User updatedUser = foundUser.get();
            updatedUser.setNickName(user.getNickName());
            updatedUser.setPersonalId(user.getPersonalId());

            log.info("Updated user: {}", updatedUser);
            return userDao.save(updatedUser);
        }
    }
}
