package hu.bsstudio.raktr.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

@Configuration
@SuppressWarnings("checkstyle:StaticVariableName")
@PropertySource("classpath:ldap.properties")
@EnableWebSecurity
@Profile("!integrationtest")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${ldap.domain}")
    private String domain;

    @Value("${ldap.url}")
    private String url;

    @Value("${ldap.rootdn}")
    private String rootDn;

    @Value("${ldap.userDn}")
    private String userDn;

    @Value("${ldap.password}")
    private String password;

    private final UserDetailsMapper userDetailsMapper;

    public WebSecurityConfig(final UserDetailsMapper userDetailsMapper) {
        this.userDetailsMapper = userDetailsMapper;
    }

    @Override
    protected final void configure(final AuthenticationManagerBuilder auth) {
        ActiveDirectoryLdapAuthenticationProvider adProvider =
            new ActiveDirectoryLdapAuthenticationProvider(domain, url, rootDn);

        adProvider.setConvertSubErrorCodesToExceptions(true);
        adProvider.setUseAuthenticationRequestCredentials(true);
        adProvider.setUserDetailsContextMapper(userDetailsMapper);

        auth.authenticationProvider(adProvider);
    }

    @Override
    protected final void configure(final HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .anyRequest().fullyAuthenticated()
            .and()
            .formLogin().defaultSuccessUrl("/api/device");
        http.csrf().disable();
    }
}
