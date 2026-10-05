package hu.bsstudio.raktr.dal.value;

import hu.bsstudio.raktr.exception.InvalidValueException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConfigDataTypeTest {

    @ParameterizedTest
    @ValueSource(strings = {"true", "false"})
    void booleanAcceptsTrueAndFalse(String value) {
        assertThatCode(() -> ConfigDataType.BOOLEAN.validate(value)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"yes", "TRUE", ""})
    void booleanRejectsOtherValues(String value) {
        assertThatThrownBy(() -> ConfigDataType.BOOLEAN.validate(value))
                .isInstanceOf(InvalidValueException.class)
                .hasMessage("Invalid BOOLEAN value: " + value);
    }

    @ParameterizedTest
    @ValueSource(strings = {"anything", ""})
    void stringAcceptsAnyValue(String value) {
        assertThatCode(() -> ConfigDataType.STRING.validate(value)).doesNotThrowAnyException();
    }

}
