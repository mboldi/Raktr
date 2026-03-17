package hu.bsstudio.raktr.support;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class JsonAssert {

    private final String actual;

    private final List<String> excludedFields = new ArrayList<>();

    public static JsonAssert assertJson(String actual) {
        return new JsonAssert(actual);
    }

    public JsonAssert excluding(String... fields) {
        this.excludedFields.addAll(List.of(fields));
        return this;
    }

    @SneakyThrows
    public void equalTo(String expected) {
        var customizations = excludedFields.stream()
                .map(field -> new Customization(field, (o1, o2) -> true))
                .toArray(Customization[]::new);

        JSONAssert.assertEquals(expected, actual, new CustomComparator(JSONCompareMode.STRICT, customizations));
    }

}
