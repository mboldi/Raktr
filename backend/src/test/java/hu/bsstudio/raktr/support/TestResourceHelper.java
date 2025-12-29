package hu.bsstudio.raktr.support;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestResourceHelper {

    @SneakyThrows
    public static String loadFileContent(String filePath) {
        var inputStream = Objects.requireNonNull(TestResourceHelper.class.getResourceAsStream(filePath),
                "File not found: " + filePath);
        return IOUtils.toString(inputStream, Charset.defaultCharset());
    }

}
