package hu.bsstudio.raktr.security;

import hu.bsstudio.raktr.model.User;
import hu.bsstudio.raktr.model.UserRole;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetails;

@NoArgsConstructor
public class LdapUser extends User implements LdapUserDetails {

    public LdapUser(final User user) {
        if (user != null) {
            this.setId(user.getId());
            this.setFamilyName(user.getFamilyName());
            this.setGivenName(user.getGivenName());
            this.setNickName(user.getNickName());
            this.setPersonalId(user.getPersonalId());
            this.setRoles(user.getRoles());
        }
    }

    @Override
    public final String getDn() {
        return null;
    }

    @Override
    public void eraseCredentials() {

    }

    @Override
    public final Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authList = new ArrayList<>();

        for (UserRole role : getRoles()) {
            authList.add(new SimpleGrantedAuthority(role.getRoleName()));
        }

        return authList;
    }

    @Override
    public final String getPassword() {
        return null;
    }

    @Override
    public final String getUsername() {
        return super.getUsername();
    }

    @Override
    public final boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public final boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public final boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public final boolean isEnabled() {
        return false;
    }
}
