package hu.bsstudio.raktr.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

final class CompositeItemTest {
    private static final Validator VALIDATOR = javax.validation.Validation.buildDefaultValidatorFactory().getValidator();

    private static final Long ID = 1L;
    private static final String NAME = "scannable_name";
    private static final String BARCODE = "barcode";

    private static final Long LOCATION_ID = 2L;
    private static final String LOCATION_NAME = "location_name";
    private static final Long DEVICE_ID = 3L;
    private static final String DEVICE_NAME = "device_name";

    private CompositeItem underTest;

    private Location location = Location.builder()
        .withId(LOCATION_ID)
        .withName(LOCATION_NAME)
        .build();

    private Device device = Device.builder()
        .withId(DEVICE_ID)
        .withName(DEVICE_NAME)
        .build();

    private CompositeItem.Builder defaultBuilder = CompositeItem.builder()
        .withId(ID)
        .withName(NAME)
        .withBarcode(BARCODE)
        .withLocation(location);

    @Test
    void testConstructorAndGetters() {
        //given
        List<Device> devices = new ArrayList<>();
        devices.add(device);

        underTest = defaultBuilder
            .withDevices(devices)
            .build();

        //when

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(NAME, underTest.getName()),
            () -> assertEquals(BARCODE, underTest.getBarcode()),
            () -> assertEquals(location, underTest.getLocation())
        );
    }

    @Test
    void testNoArgsConstructor() {
        //given
        underTest = new CompositeItem();

        //when

        //then
        assertAll(
            () -> assertNull(underTest.getId()),
            () -> assertNull(underTest.getName()),
            () -> assertNull(underTest.getBarcode()),
            () -> assertNull(underTest.getLocation()),
            () -> assertNull(underTest.getDevices())
        );
    }

    @Test
    void testValidationShouldFailForEmptyName() {
        //given
        underTest = defaultBuilder
            .withName("")
            .build();

        //when
        Set<ConstraintViolation<CompositeItem>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be blank", "name");
    }

    @Test
    void testValidationShouldFailForNullBarcode() {
        //given
        underTest = defaultBuilder
            .withBarcode(null)
            .build();

        //when
        Set<ConstraintViolation<CompositeItem>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be null", "barcode");
    }

    @Test
    void testValidationShouldFailForNullLocation() {
        //given
        underTest = defaultBuilder
            .withLocation(null)
            .build();

        //when
        Set<ConstraintViolation<CompositeItem>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be null", "location");
    }

    private <T> void validationFails(final Set<ConstraintViolation<T>> violations, final String expectedMessage, final String expectedProperty) {
        assertThat(violations.size(), is(1));
        ConstraintViolation<T> violation = violations.stream().findFirst().get();
        assertThat(violation.getMessage(), is(expectedMessage));
        assertThat(violation.getPropertyPath().toString(), is(expectedProperty));
    }
}
