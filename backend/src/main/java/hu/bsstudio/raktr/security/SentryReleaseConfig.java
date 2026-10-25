package hu.bsstudio.raktr.security;

import io.sentry.Sentry;
import io.sentry.SentryOptions;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SentryReleaseConfig {

    @Bean
    public Sentry.OptionsConfiguration<SentryOptions> sentryRelease(BuildProperties buildProperties) {
        return options -> options.setRelease("raktr-backend@" + buildProperties.getVersion());
    }

}
