package hu.bsstudio.raktr.user.service;

import hu.bsstudio.raktr.dal.entity.User;
import hu.bsstudio.raktr.dal.repository.UserRepository;
import hu.bsstudio.raktr.dto.user.UserDetailsDto;
import hu.bsstudio.raktr.dto.user.UserSummaryDto;
import hu.bsstudio.raktr.dto.user.UserUpdateDto;
import hu.bsstudio.raktr.exception.EntityNotFoundException;
import hu.bsstudio.raktr.security.RoleConstants;
import hu.bsstudio.raktr.security.SecurityService;
import hu.bsstudio.raktr.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Transactional(readOnly = true)
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

    private final SecurityService securityService;

    public List<UserSummaryDto> getUsers() {
        var users = userRepository.findAll();
        return users.stream().map(userMapper::entityToSummaryDto).toList();
    }

    public List<UserSummaryDto> getUsers(boolean canIssueRent) {
        var users = userRepository.findAll();
        return users.stream()
                .filter(user -> user.hasAnyAuthority(ROLES_ALLOWED_TO_ISSUE_RENT) == canIssueRent)
                .map(userMapper::entityToSummaryDto)
                .toList();
    }

    public UserSummaryDto getUserByUsername(String username) {
        var user = getUser(username);
        return userMapper.entityToSummaryDto(user);
    }

    public UserDetailsDto getCurrentUser() {
        var uuid = securityService.getCurrentUserUuid();
        var user = userRepository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException(User.class, uuid));
        return userMapper.entityToDetailsDto(user);
    }

    @Transactional
    public UserDetailsDto updateUser(String username, UserUpdateDto updateDto) {
        var user = getUser(username);

        securityService.checkIsOwnerOrAdmin(user.getUuid());

        userMapper.updateDtoToEntity(user, updateDto);
        userRepository.saveAndFlush(user);

        log.info("Updated User with username [{}]", username);

        return userMapper.entityToDetailsDto(user);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException(User.class, username));
    }

}
