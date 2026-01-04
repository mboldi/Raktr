package hu.bsstudio.raktr.dependency;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static hu.bsstudio.raktr.support.TestResourceHelper.loadFileContent;

@NoArgsConstructor(access = AccessLevel.NONE)
public class SsoProviderMock {

    private static final WireMockServer wireMockServer = new WireMockServer(
            options().dynamicPort().globalTemplating(true)
    );

    private static final RSAKey rsaKey = generateKeys();

    static {
        wireMockServer.start();
        stubOpenidConfiguration();
        stubJwks();
    }

    public static String getBaseUrl() {
        return wireMockServer.baseUrl() + "/application/o/raktr/";
    }

    @SneakyThrows
    public static String generateJwt(
            String uuid,
            String username,
            String familyName,
            String givenName,
            List<String> groups
    ) {
        var signer = new RSASSASigner(rsaKey);

        var claimsSet = new JWTClaimsSet.Builder()
                .subject(uuid)
                .issuer(getBaseUrl())
                .claim("preferred_username", username)
                .claim("groups", groups)
                .claim("name", givenName + " " + familyName)
                .claim("family_name", familyName)
                .claim("given_name", givenName)
                .claim("scope", "offline_access profile openid")
                .expirationTime(Date.from(Instant.now().plusSeconds(3600)))
                .issueTime(Date.from(Instant.now()))
                .build();

        var signedJWT = new SignedJWT(
                new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaKey.getKeyID()).build(),
                claimsSet);

        signedJWT.sign(signer);

        return signedJWT.serialize();
    }

    private static void stubOpenidConfiguration() {
        wireMockServer.stubFor(get(urlEqualTo("/application/o/raktr/.well-known/openid-configuration"))
                .willReturn(okJson(loadFileContent("/dependency/sso-provider/openid-configuration.json"))));
    }

    private static void stubJwks() {
        wireMockServer.stubFor(get(urlEqualTo("/application/o/raktr/jwks/"))
                .willReturn(okJson(new JWKSet(rsaKey).toString())));
    }

    @SneakyThrows
    private static RSAKey generateKeys() {
        var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        var keyPair = keyPairGenerator.generateKeyPair();

        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
    }

}
