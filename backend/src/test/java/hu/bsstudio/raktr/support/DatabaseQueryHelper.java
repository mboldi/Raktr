package hu.bsstudio.raktr.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseQueryHelper {

    private final JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper;

    public DatabaseQuery queryDatabase(String sql) {
        return new DatabaseQuery(sql, jdbcTemplate, objectMapper);
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

}
