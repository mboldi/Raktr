package hu.bsstudio.raktr.security;

import hu.bsstudio.raktr.model.User;
import hu.bsstudio.raktr.model.UserRole;
import hu.bsstudio.raktr.repository.UserRepository;
import hu.bsstudio.raktr.repository.UserRoleRepository;
import java.util.Collection;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.InetOrgPerson;
import org.springframework.security.ldap.userdetails.InetOrgPersonContextMapper;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsMapper implements UserDetailsContextMapper {

    private final UserRepository userRepository;
    private final UserRoleRepository roleDao;

    private final InetOrgPersonContextMapper ldapUserDetailsMapper = new InetOrgPersonContextMapper();

    public UserDetailsMapper(final UserRepository userRepository, final UserRoleRepository roleDao) {
        this.userRepository = userRepository;
        this.roleDao = roleDao;
    }

    @Override
    public final UserDetails mapUserFromContext(final DirContextOperations ctx,
                                                final String username,
                                                final Collection<? extends GrantedAuthority> authorities) {
        InetOrgPerson ldapPerson = (InetOrgPerson) ldapUserDetailsMapper.mapUserFromContext(ctx, username, authorities);

        User foundUser = userRepository.findByUsername(username).orElse(null);

        if (foundUser == null) {
            foundUser = User.builder()
                .withUsername(username)
                .withNickName(ldapPerson.getGivenName())
                .withPersonalId("")
                .withFamilyName(ldapPerson.getSn())
                .withGivenName(ldapPerson.getGivenName())
                .build();
        } else {
            foundUser.setGivenName(ldapPerson.getGivenName());
            foundUser.setFamilyName(ldapPerson.getSn());
            if (foundUser.getRoles() != null) {
                foundUser.getRoles().clear();
            }
        }

        for (int i = 0; i < authorities.size(); i++) {
            UserRole role = new UserRole();
            GrantedAuthority authority = (GrantedAuthority) authorities.toArray()[i];
            String authString = "ROLE_" + authority.getAuthority();
            role.setRoleName(authString);

            UserRole foundRole = roleDao.findByRoleName(authString).orElse(null);

            if (foundRole == null) {
                roleDao.save(role);

                UserRole newRole = roleDao.findByRoleName(role.getRoleName()).orElse(null);
                foundUser.addRole(newRole);
            } else {
                foundUser.addRole(foundRole);
            }
        }

        userRepository.save(foundUser);

        return new LdapUser(foundUser);
    }

    @Override
    public void mapUserToContext(final UserDetails user, final DirContextAdapter ctx) {
    }
}
