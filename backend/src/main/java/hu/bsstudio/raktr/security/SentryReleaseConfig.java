package hu.bsstudio.raktr.security;

import io.sentry.EventProcessor;
import io.sentry.Hint;
import io.sentry.SentryEvent;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Component;

@Component
public class SentryReleaseConfig implements EventProcessor {

    private final String release;

    public SentryReleaseConfig(BuildProperties buildProperties) {
        this.release = buildProperties.getName() + "@" + buildProperties.getVersion();
    }

    @Override
    public SentryEvent process(SentryEvent event, @NonNull Hint hint) {
        event.setRelease(release);
        return event;
    }

}
