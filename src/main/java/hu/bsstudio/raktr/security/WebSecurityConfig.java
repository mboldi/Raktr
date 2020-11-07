package hu.bsstudio.raktr.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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

    private final MyUserDetailsService userDetailsService;

    public WebSecurityConfig(final UserDetailsMapper userDetailsMapper, final MyUserDetailsService userDetailsService) {
        this.userDetailsMapper = userDetailsMapper;
        this.userDetailsService = userDetailsService;
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
            .cors().and().csrf().disable()
            .authorizeRequests()
            .antMatchers("/login").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilter(new JWTAuthenticationFilter(authenticationManager()))
            .addFilter(new JWTAuthorizationFilter(authenticationManager(), userDetailsService))
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @SuppressWarnings("checkstyle:DesignForExtension")
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }
}
