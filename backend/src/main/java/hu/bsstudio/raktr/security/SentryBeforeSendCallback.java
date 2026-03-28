package hu.bsstudio.raktr.security;

import hu.bsstudio.raktr.exception.AccessDeniedException;
import hu.bsstudio.raktr.exception.EntityException;
import io.sentry.Hint;
import io.sentry.SentryEvent;
import io.sentry.SentryOptions;
import org.springframework.stereotype.Component;

@Component
public class SentryBeforeSendCallback implements SentryOptions.BeforeSendCallback {

    @Override
    public SentryEvent execute(SentryEvent event, Hint hint) {
        if (isClientError(event)) {
            return null;
        }
        return event;
    }

    private boolean isClientError(SentryEvent event) {
        var throwable = event.getThrowable();
        return throwable instanceof EntityException
                || throwable instanceof AccessDeniedException
                || throwable instanceof IllegalArgumentException;
    }

}
