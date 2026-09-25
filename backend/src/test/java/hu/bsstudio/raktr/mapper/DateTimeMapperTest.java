package hu.bsstudio.raktr.mapper;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DateTimeMapperTest {

    @Test
    void returnsNullForNullInstant() {
        assertThat(DateTimeMapper.instantToOffsetDateTime(null)).isNull();
    }

    @Test
    void keepsTheInstantWhenTheLocalTimeIsAmbiguous() {
        // 02:30 occurs twice in Europe/Budapest on this date. Resolving an instant through the JVM's
        // default zone picks the earlier offset and shifts the second occurrence back by an hour.
        var instant = Instant.parse("2025-10-26T01:30:00Z");

        assertThat(DateTimeMapper.instantToOffsetDateTime(instant).toInstant()).isEqualTo(instant);
    }

}
