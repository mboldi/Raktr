package hu.bsstudio.raktr.security;

import hu.bsstudio.raktr.model.User;
import hu.bsstudio.raktr.model.UserRole;
import hu.bsstudio.raktr.repository.UserRepository;
import hu.bsstudio.raktr.repository.UserRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@SuppressWarnings("checkstyle:StaticVariableName")
@EnableWebSecurity
@Profile("!integrationtest")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final MyUserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public WebSecurityConfig(final MyUserDetailsService userDetailsService,
                             final UserRepository userRepository,
                             final UserRoleRepository userRoleRepository) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    protected final void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .passwordEncoder(passwordEncoder())
                .withUser("mboldi")
                .password(passwordEncoder().encode("admin"))
                .roles("Stúdiós")
                .roles("VEZETOSEG")
                .roles("ADMIN")
                .roles("BSS");

        addBoldi();
    }

    @Override
    protected final void configure(final HttpSecurity http) throws Exception {
        http
            .addFilterBefore(corsFilter(), JWTAuthenticationFilter.class)
            .csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            .antMatchers("/login").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilter(new JWTAuthenticationFilter(authenticationManager()))
            .addFilter(new JWTAuthorizationFilter(authenticationManager(), userDetailsService))
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @SuppressWarnings("checkstyle:DesignForExtension")
    @Bean
    CorsFilter corsFilter() {
        return new CorsFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return passwordEncoder;
    }

    private void addBoldi() {
        User mboldiUser = userRepository.findByUsername("mboldi").orElse(null);

        if (mboldiUser == null) {
            log.info("MBoldi not found, creating...");

            mboldiUser = User.builder()
                    .withUsername("mboldi")
                    .withNickName("ifjB")
                    .withPersonalId("")
                    .withFamilyName("Márta")
                    .withGivenName("Boldizsár")
                    .build();

            UserRole adminRole = new UserRole.Builder()
                    .withRoleName("ROLE_Admin")
                    .build();

            UserRole studiosRole = new UserRole.Builder()
                    .withRoleName("ROLE_Stúdiós")
                    .build();

            userRoleRepository.save(adminRole);
            userRoleRepository.save(studiosRole);

            mboldiUser.addRole(userRoleRepository.findByRoleName("ROLE_Admin").get());
            mboldiUser.addRole(userRoleRepository.findByRoleName("ROLE_Stúdiós").get());

            userRepository.save(mboldiUser);
        }
    }
}
