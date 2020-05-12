package hu.bsstudio.raktr.model;

import static hu.bsstudio.raktr.model.BackStatus.BACK;
import static hu.bsstudio.raktr.model.BackStatus.OUT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

public class RentItemTest {
    private static final Validator VALIDATOR = javax.validation.Validation.buildDefaultValidatorFactory().getValidator();

    public static final Long ID = 1L;
    public static final BackStatus BACK_STATUS = OUT;
    public static final Integer OUT_QUANTITY = 1;

    public static final Long OTHER_ID = 2L;
    public static final BackStatus OTHER_BACK_STATUS = BACK;
    public static final Integer OTHER_OUT_QUANTITY = 2;

    public static final long DEVICE_ID = 3L;
    public static final long OTHER_DEVICE_ID = 4L;

    private Device device = Device.builder()
        .withId(DEVICE_ID)
        .build();

    private Device otherDevice = Device.builder()
        .withId(OTHER_DEVICE_ID)
        .build();

    private RentItem underTest;

    private RentItem.Builder defaultBuilder = RentItem.builder()
        .withId(ID)
        .withBackStatus(BACK_STATUS)
        .withOutQuantity(OUT_QUANTITY)
        .withScannable(device);

    @Test
    void testConstructorAndGetters() {
        //given
        underTest = defaultBuilder
            .build();

        //when

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(BACK_STATUS, underTest.getBackStatus()),
            () -> assertEquals(OUT_QUANTITY, underTest.getOutQuantity()),
            () -> assertEquals(device, underTest.getScannable())
        );
    }

    @Test
    void testDefaultConstructor() {
        //given
        underTest = new RentItem();

        //when

        //then
        assertAll(
            () -> assertNull(underTest.getId()),
            () -> assertNull(underTest.getBackStatus()),
            () -> assertNull(underTest.getOutQuantity()),
            () -> assertNull(underTest.getScannable())
        );
    }

    @Test
    void testValidationShouldFailForNullBackStatus() {
        //given
        underTest = defaultBuilder
            .withBackStatus(null)
            .build();

        //when
        Set<ConstraintViolation<RentItem>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be null", "backStatus");
    }

    @Test
    void testValidationShouldFailForNullOutQuantity() {
        //given
        underTest = defaultBuilder
            .withOutQuantity(null)
            .build();

        //when
        Set<ConstraintViolation<RentItem>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be null", "outQuantity");
    }

    @Test
    void testSetDevice() {
        //given
        underTest = defaultBuilder
            .build();

        //when
        underTest.setScannable(otherDevice);

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(otherDevice, underTest.getScannable()),
            () -> assertEquals(BACK_STATUS, underTest.getBackStatus()),
            () -> assertEquals(OUT_QUANTITY, underTest.getOutQuantity())
        );
    }

    @Test
    void testSetBackStatus() {
        //given
        underTest = defaultBuilder
            .build();

        //when
        underTest.setBackStatus(OTHER_BACK_STATUS);

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(device, underTest.getScannable()),
            () -> assertEquals(OTHER_BACK_STATUS, underTest.getBackStatus()),
            () -> assertEquals(OUT_QUANTITY, underTest.getOutQuantity())
        );
    }

    @Test
    void testSetOutQuantity() {
        //given
        underTest = defaultBuilder
            .build();

        //when
        underTest.setOutQuantity(OTHER_OUT_QUANTITY);

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(device, underTest.getScannable()),
            () -> assertEquals(BACK_STATUS, underTest.getBackStatus()),
            () -> assertEquals(OTHER_OUT_QUANTITY, underTest.getOutQuantity())
        );
    }

    private <T> void validationFails(final Set<ConstraintViolation<T>> violations, final String expectedMessage, final String expectedProperty) {
        assertThat(violations.size(), is(1));
        ConstraintViolation<T> violation = violations.stream().findFirst().get();
        assertThat(violation.getMessage(), is(expectedMessage));
        assertThat(violation.getPropertyPath().toString(), is(expectedProperty));
    }
}
