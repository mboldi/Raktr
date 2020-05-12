package hu.bsstudio.raktr.security;

import hu.bsstudio.raktr.dao.UserDao;
import hu.bsstudio.raktr.model.User;
import hu.bsstudio.raktr.model.UserRole;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
public class MyUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    public MyUserDetailsService(final UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public final UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

        User user = userDao.findByUsername(username).orElse(null);

        if (user == null) {
            throw new UsernameNotFoundException(username + " username not found");
        }

        return new org.springframework.security.core.userdetails.User(
            user.getUsername(), " ", user.isEnabled(),
            true, true, true,
            addGrantedAuthorities(user.getRoles())
        );
    }

    private Collection<GrantedAuthority> addGrantedAuthorities(final List<UserRole> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (var role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getRoleName()));
        }

        return authorities;
    }
}
