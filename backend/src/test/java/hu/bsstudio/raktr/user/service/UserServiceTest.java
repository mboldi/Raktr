package hu.bsstudio.raktr.user.service;

import hu.bsstudio.raktr.dal.repository.UserRepository;
import hu.bsstudio.raktr.exception.EntityNotFoundException;
import hu.bsstudio.raktr.security.SecurityService;
import hu.bsstudio.raktr.user.mapper.UserMapper;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class UserServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);

    private final UserMapper userMapper = mock(UserMapper.class);

    private final SecurityService securityService = mock(SecurityService.class);

    private final UserService userService = new UserService(userRepository, userMapper, securityService);

    @Test
    void getCurrentUserFailsWhenUserIsNotSynced() {
        var uuid = UUID.randomUUID();
        when(securityService.getCurrentUserUuid()).thenReturn(uuid);
        when(userRepository.findById(uuid)).thenReturn(Optional.empty());

        assertThatThrownBy(userService::getCurrentUser)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(uuid.toString());
        verifyNoInteractions(userMapper);
    }

}
