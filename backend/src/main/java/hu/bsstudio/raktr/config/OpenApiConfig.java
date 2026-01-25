package hu.bsstudio.raktr.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "oauth2";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BSS Raktr API")
                        .version("1.0")
                        .description("Budavári Schönherz Stúdió Inventory Management System API"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, createOAuth2SecurityScheme()));
    }

    private SecurityScheme createOAuth2SecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.OAUTH2)
                .flows(new OAuthFlows()
                        .authorizationCode(new OAuthFlow()
                                .authorizationUrl("https://login.bsstudio.hu/application/o/authorize/")
                                .tokenUrl("https://login.bsstudio.hu/application/o/token/")
                                .scopes(new Scopes()
                                        .addString("openid", "OpenID Connect scope")
                                        .addString("profile", "User profile information"))));
    }

}
