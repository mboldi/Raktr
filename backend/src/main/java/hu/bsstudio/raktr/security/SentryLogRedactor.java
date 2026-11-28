package hu.bsstudio.raktr.security;

import io.sentry.SentryLogEvent;
import io.sentry.SentryLogEventAttributeValue;
import io.sentry.SentryOptions;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/** Redacts emails, phone numbers and IP addresses from the log lines sent to Sentry. */
@Component
public class SentryLogRedactor implements SentryOptions.Logs.BeforeSendLogCallback {

    private static final String REDACTED = "[redacted]";

    // The same patterns as the scrubbing in request-manager and backstage.
    private static final List<Pattern> PATTERNS = List.of(
        Pattern.compile("[\\w.+-]+@[\\w-]+(?:\\.[\\w-]+)+"),
        Pattern.compile("(?:\\+|00)\\d[\\d\\s\\-./()]{6,}\\d|\\b06[\\s\\-./]?\\d{1,2}[\\s\\-./]?\\d{3}[\\s\\-./]?\\d{3,4}\\b"),
        Pattern.compile("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b"),
        // Requires "::" or eight groups, so clock times like 12:30:05 survive.
        Pattern.compile(
            "(?<![\\w:])(?:(?:[0-9a-f]{1,4}:){7}[0-9a-f]{1,4}"
                + "|(?:[0-9a-f]{1,4}:){0,6}[0-9a-f]{0,4}::(?:[0-9a-f]{1,4}:){0,6}[0-9a-f]{0,4})(?![\\w:])",
            Pattern.CASE_INSENSITIVE
        )
    );

    public static String redact(String text) {
        var result = text;
        for (var pattern : PATTERNS) {
            result = pattern.matcher(result).replaceAll(REDACTED);
        }
        return result;
    }

    @Override
    public SentryLogEvent execute(@NonNull SentryLogEvent event) {
        if (event.getBody() != null) {
            event.setBody(redact(event.getBody()));
        }
        var attributes = event.getAttributes();
        if (attributes != null) {
            var redacted = new HashMap<String, SentryLogEventAttributeValue>();
            attributes.forEach((key, attribute) -> redacted.put(
                key,
                attribute.getValue() instanceof String value
                    ? new SentryLogEventAttributeValue(attribute.getType(), redact(value))
                    : attribute
            ));
            event.setAttributes(redacted);
        }
        return event;
    }

}
