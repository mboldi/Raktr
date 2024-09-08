package hu.bsstudio.raktr.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Slf4j
@Configuration
@RequiredArgsConstructor
@SuppressWarnings("checkstyle:StaticVariableName")
@EnableWebSecurity
@Profile("!integrationtest")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final BssLoginJwtAuthenticationConverter bssLoginJwtAuthenticationConverter;

    @Override
    protected final void configure(final HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.OPTIONS).permitAll()
            .anyRequest().authenticated()
            .and()
            .oauth2ResourceServer().jwt();
    }

    @SuppressWarnings("checkstyle:DesignForExtension")
    @Bean
    CorsFilter corsFilter() {
        return new CorsFilter();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(bssLoginJwtAuthenticationConverter);
        return jwtAuthenticationConverter;
    }

}
