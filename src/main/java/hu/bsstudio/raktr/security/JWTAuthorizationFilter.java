package hu.bsstudio.raktr.security;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static hu.bsstudio.raktr.security.SecurityConstants.HEADER_STRING;
import static hu.bsstudio.raktr.security.SecurityConstants.SECRET;
import static hu.bsstudio.raktr.security.SecurityConstants.TOKEN_PREFIX;

import com.auth0.jwt.JWT;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final MyUserDetailsService userDetailsService;

    public JWTAuthorizationFilter(final AuthenticationManager authManager, final MyUserDetailsService userDetailsService) {
        super(authManager);
        this.userDetailsService = userDetailsService;
    }

    @SuppressWarnings("checkstyle:DesignForExtension")
    @Override
    protected void doFilterInternal(final HttpServletRequest req,
                                    final HttpServletResponse res,
                                    final FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(final HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            // parse the token.
            String user = JWT.require(HMAC512(SECRET.getBytes()))
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""))
                .getSubject();

            if (user != null) {
                UserDetails principal = userDetailsService.loadUserByUsername(user);
                return new UsernamePasswordAuthenticationToken(user, null, principal.getAuthorities());
            }
            return null;
        }
        return null;
    }
}
