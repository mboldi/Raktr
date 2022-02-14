package hu.bsstudio.raktr.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static hu.bsstudio.raktr.security.SecurityConstants.EXPIRATION_TIME;
import static hu.bsstudio.raktr.security.SecurityConstants.HEADER_STRING;
import static hu.bsstudio.raktr.security.SecurityConstants.SECRET;
import static hu.bsstudio.raktr.security.SecurityConstants.TOKEN_PREFIX;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.bsstudio.raktr.model.UserLoginData;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(final AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @SuppressWarnings("checkstyle:DesignForExtension")
    @Override
    public Authentication attemptAuthentication(final HttpServletRequest req,
                                                final HttpServletResponse res) {
        try {
            UserLoginData creds = new ObjectMapper()
                .readValue(req.getInputStream(), UserLoginData.class);

            //log.info("User credentials wanting to sign in: {}", creds);

            return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    creds.getUsername(),
                    creds.getPassword(),
                    new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("checkstyle:DesignForExtension")
    @Override
    protected void successfulAuthentication(final HttpServletRequest req,
                                            final HttpServletResponse res,
                                            final FilterChain chain,
                                            final Authentication auth) throws IOException, ServletException {

        Date expiresAt = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

        String token = JWT.create()
            .withSubject(((LdapUser) auth.getPrincipal()).getUsername())
            .withExpiresAt(expiresAt)
            .sign(HMAC512(SECRET.getBytes()));

        DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm a z");

        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
        res.getWriter().println("{\"token\": \"" + token + "\", \n\"expiresAt\": \"" + df.format(expiresAt) + "\" }");
    }
}
