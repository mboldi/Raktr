package hu.bsstudio.raktr.security;

import io.sentry.SentryLogEvent;
import io.sentry.SentryLogEventAttributeValue;
import io.sentry.SentryLogLevel;
import io.sentry.protocol.SentryId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class SentryLogRedactorTest {

    @ParameterizedTest
    @ValueSource(strings = {"kovacs.anna@example.com", "+36 30 123 4567", "06 30 123 4567", "10.1.2.3", "2001:db8::1"})
    void redactsPersonalData(String value) {
        assertThat(SentryLogRedactor.redact("before " + value + " after")).isEqualTo("before [redacted] after");
    }

    @Test
    void keepsUsernameIdPathAndClockTime() {
        var text = "kovacs.anna 4242 /api/devices/17 12:30:05";
        assertThat(SentryLogRedactor.redact(text)).isEqualTo(text);
    }

    @Test
    void redactsPostgresUniqueViolation() {
        assertThat(SentryLogRedactor.redact("Key (email)=(kovacs.anna@example.com) already exists."))
            .isEqualTo("Key (email)=([redacted]) already exists.");
    }

    @Test
    void redactsBodyAndStringAttributes() {
        var event = new SentryLogEvent(new SentryId(), 0.0, "Sent to kovacs.anna@example.com", SentryLogLevel.INFO);
        event.setAttribute("sentry.message.parameter.0", new SentryLogEventAttributeValue("string", "kovacs.anna@example.com"));
        event.setAttribute("device.id", new SentryLogEventAttributeValue("integer", 17));

        var result = new SentryLogRedactor().execute(event);

        assertThat(result.getBody()).isEqualTo("Sent to [redacted]");
        assertThat(result.getAttributes().get("sentry.message.parameter.0").getValue()).isEqualTo("[redacted]");
        assertThat(result.getAttributes().get("device.id").getValue()).isEqualTo(17);
    }
}
