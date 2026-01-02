package hu.bsstudio.raktr.user.service;

import hu.bsstudio.raktr.dal.entity.User;
import hu.bsstudio.raktr.dal.repository.UserRepository;
import hu.bsstudio.raktr.dto.user.UserDetailsDto;
import hu.bsstudio.raktr.dto.user.UserUpdateDto;
import hu.bsstudio.raktr.exception.ObjectNotFoundException;
import hu.bsstudio.raktr.security.RoleConstants;
import hu.bsstudio.raktr.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private static final List<String> ROLES_ALLOWED_TO_ISSUE_RENT = List.of(
            RoleConstants.ALUMNI,
            RoleConstants.MEMBER,
            RoleConstants.MEMBER_CANDIDATE
    );

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public List<UserDetailsDto> getUsers() {
        var users = userRepository.findAll();
        return users.stream().map(userMapper::entityToDetailsDto).toList();
    }

    public List<UserDetailsDto> getUsers(boolean canIssueRent) {
        var users = userRepository.findAll();
        return users.stream()
                .filter(user -> user.hasAnyAuthority(ROLES_ALLOWED_TO_ISSUE_RENT) == canIssueRent)
                .map(userMapper::entityToDetailsDto)
                .toList();
    }

    public UserDetailsDto getUserByUsername(String username) {
        var user = getUser(username);
        return userMapper.entityToDetailsDto(user);
    }

    @Transactional
    public UserDetailsDto updateUser(String username, UserUpdateDto updateDto) {
        var user = getUser(username);

        userMapper.updateDtoToEntity(user, updateDto);
        userRepository.saveAndFlush(user);

        log.info("Updated User with username [{}]", username);

        return userMapper.entityToDetailsDto(user);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username).orElseThrow(ObjectNotFoundException::new);
    }

}
