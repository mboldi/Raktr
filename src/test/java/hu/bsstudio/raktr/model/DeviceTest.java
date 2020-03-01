package hu.bsstudio.raktr.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

final class DeviceTest {
    private static final Validator VALIDATOR = javax.validation.Validation.buildDefaultValidatorFactory().getValidator();

    private static final Long ID = 1L;
    private static final String NAME = "device";
    private static final String MAKER = "maker";
    private static final String TYPE = "type";
    private static final String SERIAL = "serial";
    private static final Integer VALUE = 100;
    private static final String BARCODE = "barcode";
    private static final Integer WEIGHT = 1000;
    private static final String LOCATION_NAME = "location";
    private static final Integer STATUS = 5;
    private static final String CATEGORY_NAME = "category";

    private static final String OTHER_NAME = "device2";
    private static final String OTHER_MAKER = "maker2";
    private static final String OTHER_TYPE = "type2";
    private static final String OTHER_SERIAL = "serial2";
    private static final Integer OTHER_VALUE = 102;
    private static final String OTHER_BARCODE = "barcode2";
    private static final Integer OTHER_WEIGHT = 1002;
    private static final Integer OTHER_STATUS = 4;
    public static final int STATUS_NEGATIV = -4;
    public static final int STATUS_TOO_BIG = 10;

    private Device underTest;
    private Location location = Location.builder()
        .withName(LOCATION_NAME)
        .build();
    private Category category = Category.builder()
        .withName(CATEGORY_NAME)
        .build();

    private Device.Builder defaultBuilder = Device.builder()
        .withId(ID)
        .withName(NAME)
        .withMaker(MAKER)
        .withType(TYPE)
        .withSerial(SERIAL)
        .withValue(VALUE)
        .withBarcode(BARCODE)
        .withWeight(WEIGHT)
        .withLocation(location)
        .withStatus(STATUS)
        .withCategory(category);

    @Test
    void testConstructorAndGetters() {
        //given
        underTest = defaultBuilder.build();

        //when

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(NAME, underTest.getName()),
            () -> assertEquals(MAKER, underTest.getMaker()),
            () -> assertEquals(TYPE, underTest.getType()),
            () -> assertEquals(SERIAL, underTest.getSerial()),
            () -> assertEquals(VALUE, underTest.getValue()),
            () -> assertEquals(BARCODE, underTest.getBarcode()),
            () -> assertEquals(WEIGHT, underTest.getWeight()),
            () -> assertEquals(STATUS, underTest.getStatus()),
            () -> assertEquals(CATEGORY_NAME, underTest.getCategory().getName()),
            () -> assertEquals(LOCATION_NAME, underTest.getLocation().getName())
        );
    }

    @Test
    void testNoArgsConstructor() {
        //given
        underTest = new Device();

        //when

        //then
        assertAll(
            () -> assertNull(underTest.getId()),
            () -> assertNull(underTest.getName()),
            () -> assertNull(underTest.getMaker()),
            () -> assertNull(underTest.getType()),
            () -> assertNull(underTest.getSerial()),
            () -> assertNull(underTest.getValue()),
            () -> assertNull(underTest.getBarcode()),
            () -> assertNull(underTest.getWeight()),
            () -> assertNull(underTest.getStatus())
        );
    }

    @Test
    void testValidationShouldFailForEmptyName() {
        //given
        underTest = defaultBuilder
            .withName("")
            .build();

        //when
        Set<ConstraintViolation<Device>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be blank", "name");
    }

    @Test
    void testValidationShouldFailForToSmallStatus() {
        //given
        underTest = defaultBuilder
            .withStatus(STATUS_NEGATIV)
            .build();

        //when
        Set<ConstraintViolation<Device>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must be greater than or equal to 0", "status");
    }

    @Test
    void testValidationShouldFailForToBigStatus() {
        //given
        underTest = defaultBuilder
            .withStatus(STATUS_TOO_BIG)
            .build();

        //when
        Set<ConstraintViolation<Device>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must be less than or equal to 5", "status");
    }

    @Test
    void testValidationShouldFailForNullMaker() {
        //given
        underTest = defaultBuilder
            .withMaker(null)
            .build();

        //when
        Set<ConstraintViolation<Device>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be null", "maker");
    }

    @Test
    void testValidationShouldFailForNullType() {
        //given
        underTest = defaultBuilder
            .withType(null)
            .build();

        //when
        Set<ConstraintViolation<Device>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be null", "type");
    }

    @Test
    void testValidationShouldFailForNullSerial() {
        //given
        underTest = defaultBuilder
            .withSerial(null)
            .build();

        //when
        Set<ConstraintViolation<Device>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be null", "serial");
    }

    @Test
    void testValidationShouldFailForNullValue() {
        //given
        underTest = defaultBuilder
            .withValue(null)
            .build();

        //when
        Set<ConstraintViolation<Device>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be null", "value");
    }

    @Test
    void testValidationShouldFailForNullBarcode() {
        //given
        underTest = defaultBuilder
            .withBarcode(null)
            .build();

        //when
        Set<ConstraintViolation<Device>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be null", "barcode");
    }

    @Test
    void testValidationShouldFailForTooSmallWeight() {
        //given
        underTest = defaultBuilder
            .withWeight(-1)
            .build();

        //when
        Set<ConstraintViolation<Device>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must be greater than or equal to 0", "weight");
    }

    @Test
    void testValidationShouldFailForNullLocation() {
        //given
        underTest = defaultBuilder
            .withLocation(null)
            .build();

        //when
        Set<ConstraintViolation<Device>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be null", "location");
    }

    @Test
    void testValidationShouldFailForNullCategory() {
        //given
        underTest = defaultBuilder
            .withCategory(null)
            .build();

        //when
        Set<ConstraintViolation<Device>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be null", "category");
    }

    @Test
    void testSetName() {
        //given
        underTest = defaultBuilder
            .build();

        //when
        underTest.setName(OTHER_NAME);

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(OTHER_NAME, underTest.getName()),
            () -> assertEquals(MAKER, underTest.getMaker()),
            () -> assertEquals(TYPE, underTest.getType()),
            () -> assertEquals(SERIAL, underTest.getSerial()),
            () -> assertEquals(VALUE, underTest.getValue()),
            () -> assertEquals(BARCODE, underTest.getBarcode()),
            () -> assertEquals(WEIGHT, underTest.getWeight()),
            () -> assertEquals(STATUS, underTest.getStatus())
        );
    }

    @Test
    void testSetMaker() {
        //given
        underTest = defaultBuilder
            .build();

        //when
        underTest.setMaker(OTHER_MAKER);

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(NAME, underTest.getName()),
            () -> assertEquals(OTHER_MAKER, underTest.getMaker()),
            () -> assertEquals(TYPE, underTest.getType()),
            () -> assertEquals(SERIAL, underTest.getSerial()),
            () -> assertEquals(VALUE, underTest.getValue()),
            () -> assertEquals(BARCODE, underTest.getBarcode()),
            () -> assertEquals(WEIGHT, underTest.getWeight()),
            () -> assertEquals(STATUS, underTest.getStatus())
        );
    }

    @Test
    void testSetType() {
        //given
        underTest = defaultBuilder
            .build();

        //when
        underTest.setType(OTHER_TYPE);

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(NAME, underTest.getName()),
            () -> assertEquals(MAKER, underTest.getMaker()),
            () -> assertEquals(OTHER_TYPE, underTest.getType()),
            () -> assertEquals(SERIAL, underTest.getSerial()),
            () -> assertEquals(VALUE, underTest.getValue()),
            () -> assertEquals(BARCODE, underTest.getBarcode()),
            () -> assertEquals(WEIGHT, underTest.getWeight()),
            () -> assertEquals(STATUS, underTest.getStatus())
        );
    }

    @Test
    void testSetSerial() {
        //given
        underTest = defaultBuilder
            .build();

        //when
        underTest.setSerial(OTHER_SERIAL);

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(NAME, underTest.getName()),
            () -> assertEquals(MAKER, underTest.getMaker()),
            () -> assertEquals(TYPE, underTest.getType()),
            () -> assertEquals(OTHER_SERIAL, underTest.getSerial()),
            () -> assertEquals(VALUE, underTest.getValue()),
            () -> assertEquals(BARCODE, underTest.getBarcode()),
            () -> assertEquals(WEIGHT, underTest.getWeight()),
            () -> assertEquals(STATUS, underTest.getStatus())
        );
    }

    @Test
    void testSetValue() {
        //given
        underTest = defaultBuilder
            .build();

        //when
        underTest.setValue(OTHER_VALUE);

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(NAME, underTest.getName()),
            () -> assertEquals(MAKER, underTest.getMaker()),
            () -> assertEquals(TYPE, underTest.getType()),
            () -> assertEquals(SERIAL, underTest.getSerial()),
            () -> assertEquals(OTHER_VALUE, underTest.getValue()),
            () -> assertEquals(BARCODE, underTest.getBarcode()),
            () -> assertEquals(WEIGHT, underTest.getWeight()),
            () -> assertEquals(STATUS, underTest.getStatus())
        );
    }

    @Test
    void testSetBarcode() {
        //given
        underTest = defaultBuilder
            .build();

        //when
        underTest.setBarcode(OTHER_BARCODE);

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(NAME, underTest.getName()),
            () -> assertEquals(MAKER, underTest.getMaker()),
            () -> assertEquals(TYPE, underTest.getType()),
            () -> assertEquals(SERIAL, underTest.getSerial()),
            () -> assertEquals(VALUE, underTest.getValue()),
            () -> assertEquals(OTHER_BARCODE, underTest.getBarcode()),
            () -> assertEquals(WEIGHT, underTest.getWeight()),
            () -> assertEquals(STATUS, underTest.getStatus())
        );
    }

    @Test
    void testSetWeight() {
        //given
        underTest = defaultBuilder
            .build();

        //when
        underTest.setWeight(OTHER_WEIGHT);

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(NAME, underTest.getName()),
            () -> assertEquals(MAKER, underTest.getMaker()),
            () -> assertEquals(TYPE, underTest.getType()),
            () -> assertEquals(SERIAL, underTest.getSerial()),
            () -> assertEquals(VALUE, underTest.getValue()),
            () -> assertEquals(BARCODE, underTest.getBarcode()),
            () -> assertEquals(OTHER_WEIGHT, underTest.getWeight()),
            () -> assertEquals(STATUS, underTest.getStatus())
        );
    }

    @Test
    void testSetStatus() {
        //given
        underTest = defaultBuilder
            .build();

        //when
        underTest.setStatus(OTHER_STATUS);

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(NAME, underTest.getName()),
            () -> assertEquals(MAKER, underTest.getMaker()),
            () -> assertEquals(TYPE, underTest.getType()),
            () -> assertEquals(SERIAL, underTest.getSerial()),
            () -> assertEquals(VALUE, underTest.getValue()),
            () -> assertEquals(BARCODE, underTest.getBarcode()),
            () -> assertEquals(WEIGHT, underTest.getWeight()),
            () -> assertEquals(OTHER_STATUS, underTest.getStatus())
        );
    }


    private <T> void validationFails(final Set<ConstraintViolation<T>> violations, final String expectedMessage, final String expectedProperty) {
        assertThat(violations.size(), is(1));
        ConstraintViolation<T> violation = violations.stream().findFirst().get();
        assertThat(violation.getMessage(), is(expectedMessage));
        assertThat(violation.getPropertyPath().toString(), is(expectedProperty));
    }
}
