package hu.bsstudio.raktr.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class DatabaseQueryHelper {

    private static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    private final JdbcTemplate jdbcTemplate;

    public DatabaseQuery queryDatabase(String sql) {
        return new DatabaseQuery(sql, jdbcTemplate, OBJECT_MAPPER);
    }

    @RequiredArgsConstructor
    public static class DatabaseQuery {

        private final String sql;

        private final JdbcTemplate jdbcTemplate;

        private final ObjectMapper objectMapper;

        @SneakyThrows
        public JsonAssert assertRowsAsJson() {
            var rows = jdbcTemplate.queryForList(sql);
            String json = objectMapper.writeValueAsString(rows);
            return new JsonAssert(json);
        }

        @SneakyThrows
        public JsonAssert assertRowAsJson() {
            var row = jdbcTemplate.queryForMap(sql);
            String json = objectMapper.writeValueAsString(row);
            return new JsonAssert(json);
        }

        public RowCountAssert assertRowCount() {
            var result = jdbcTemplate.queryForObject(sql, Integer.class);
            return new RowCountAssert(result != null ? result : 0);
        }

    }

    private static final class TimestampSerializer extends StdSerializer<Timestamp> {
        TimestampSerializer() {
            super(Timestamp.class);
        }

        @Override
        @SneakyThrows
        public void serialize(Timestamp value, JsonGenerator generator, SerializerProvider provider) {
            var offsetDateTime = value.toLocalDateTime().atZone(ZoneOffset.UTC).toOffsetDateTime();
            generator.writeString(offsetDateTime.format(DateTimeFormatter.ISO_DATE_TIME));
        }
    }

    private static ObjectMapper createObjectMapper() {
        var objectMapper = new ObjectMapper();
        var module = new SimpleModule("DatabaseQueryModule");
        module.addSerializer(Timestamp.class, new TimestampSerializer());
        objectMapper.registerModule(module);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

}
