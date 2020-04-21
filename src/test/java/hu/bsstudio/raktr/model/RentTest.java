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

public class RentTest {
    public static final long ID = 1L;
    public static final String DESTINATION = "destination";
    public static final String ISSUER = "issuer";
    public static final String RENTER = "renter";
    public static final String OUTDATE = "outdate";
    public static final String EXPBACKDATE = "expbackdate";
    public static final String ACTBACKDATE = "actbackdate";
    public static final String OTHER_DESTINATION = "destination2";
    public static final String OTHER_ISSUER = "issuer2";
    public static final String OTHER_RENTER = "renter2";
    public static final String OTHER_OUTDATE = "outdate2";
    public static final String OTHER_EXPBACKDATE = "expbackdate2";
    public static final String OTHER_ACTBACKDATE = "actbackdate2";
    public static final Long DEVICE_ID = 3L;
    public static final Long RENT_ITEM_ID = 4L;
    private static final Validator VALIDATOR = javax.validation.Validation.buildDefaultValidatorFactory().getValidator();
    private Rent underTest;

    private final Device device = Device.builder()
        .withId(DEVICE_ID)
        .build();

    private final RentItem rentItem = RentItem.builder()
        .withId(RENT_ITEM_ID)
        .withScannable(device)
        .build();

    private final Rent.Builder defaultBuilder = Rent.builder()
        .withId(ID)
        .withDestination(DESTINATION)
        .withIssuer(ISSUER)
        .withRenter(RENTER)
        .withOutDate(OUTDATE)
        .withExpBackDate(EXPBACKDATE)
        .withActBackDate(ACTBACKDATE)
        .withRentItems(new ArrayList<>());

    @Test
    void testConstructorAndGetters() {
        //given
        List<RentItem> rentItems = new ArrayList<>();
        rentItems.add(rentItem);

        underTest = defaultBuilder
            .withRentItems(rentItems)
            .build();

        //when

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(ISSUER, underTest.getIssuer()),
            () -> assertEquals(RENTER, underTest.getRenter()),
            () -> assertEquals(DESTINATION, underTest.getDestination()),
            () -> assertEquals(OUTDATE, underTest.getOutDate()),
            () -> assertEquals(EXPBACKDATE, underTest.getExpBackDate()),
            () -> assertEquals(ACTBACKDATE, underTest.getActBackDate()),
            () -> assertEquals(rentItems, underTest.getRentItems())
        );
    }

    @Test
    void testNoArgsConstructor() {
        //given
        underTest = new Rent();

        //when

        //then
        assertAll(
            () -> assertNull(underTest.getId()),
            () -> assertNull(underTest.getIssuer()),
            () -> assertNull(underTest.getRenter()),
            () -> assertNull(underTest.getDestination()),
            () -> assertNull(underTest.getOutDate()),
            () -> assertNull(underTest.getExpBackDate()),
            () -> assertNull(underTest.getActBackDate()),
            () -> assertNull(underTest.getRentItems())
        );
    }

    @Test
    void testValidationFailsForEmptyDestination() {
        //given
        underTest = defaultBuilder
            .withDestination("")
            .build();

        //when
        Set<ConstraintViolation<Rent>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be blank", "destination");
    }

    @Test
    void testValidationFailsForEmptyIssuer() {
        //given
        underTest = defaultBuilder
            .withIssuer("")
            .build();

        //when
        Set<ConstraintViolation<Rent>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be blank", "issuer");
    }

    @Test
    void testValidationFailsForEmptyRenter() {
        //given
        underTest = defaultBuilder
            .withRenter("")
            .build();

        //when
        Set<ConstraintViolation<Rent>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be blank", "renter");
    }

    @Test
    void testValidationFailsForEmptyOutDate() {
        //given
        underTest = defaultBuilder
            .withOutDate("")
            .build();

        //when
        Set<ConstraintViolation<Rent>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be blank", "outDate");
    }

    @Test
    void testValidationFailsForEmptyExpBackDate() {
        //given
        underTest = defaultBuilder
            .withExpBackDate("")
            .build();

        //when
        Set<ConstraintViolation<Rent>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be blank", "expBackDate");
    }

    @Test
    void testValidationFailsForNullActBackDate() {
        //given
        underTest = defaultBuilder
            .withActBackDate(null)
            .build();

        //when
        Set<ConstraintViolation<Rent>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be null", "actBackDate");
    }

    @Test
    void testSetIssuer() {
        //given
        underTest = defaultBuilder
            .build();

        //when
        underTest.setIssuer(OTHER_ISSUER);

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(OTHER_ISSUER, underTest.getIssuer()),
            () -> assertEquals(DESTINATION, underTest.getDestination()),
            () -> assertEquals(OUTDATE, underTest.getOutDate()),
            () -> assertEquals(EXPBACKDATE, underTest.getExpBackDate()),
            () -> assertEquals(ACTBACKDATE, underTest.getActBackDate())
        );
    }

    @Test
    void testSetDestination() {
        //given
        underTest = defaultBuilder
            .build();

        //when
        underTest.setDestination(OTHER_DESTINATION);

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(ISSUER, underTest.getIssuer()),
            () -> assertEquals(OTHER_DESTINATION, underTest.getDestination()),
            () -> assertEquals(OUTDATE, underTest.getOutDate()),
            () -> assertEquals(EXPBACKDATE, underTest.getExpBackDate()),
            () -> assertEquals(ACTBACKDATE, underTest.getActBackDate())
        );
    }

    @Test
    void testSetOutDate() {
        //given
        underTest = defaultBuilder
            .build();

        //when
        underTest.setOutDate(OTHER_OUTDATE);

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(ISSUER, underTest.getIssuer()),
            () -> assertEquals(DESTINATION, underTest.getDestination()),
            () -> assertEquals(OTHER_OUTDATE, underTest.getOutDate()),
            () -> assertEquals(EXPBACKDATE, underTest.getExpBackDate()),
            () -> assertEquals(ACTBACKDATE, underTest.getActBackDate())
        );
    }

    @Test
    void testSetExpBackDate() {
        //given
        underTest = defaultBuilder
            .build();

        //when
        underTest.setExpBackDate(OTHER_EXPBACKDATE);

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(ISSUER, underTest.getIssuer()),
            () -> assertEquals(DESTINATION, underTest.getDestination()),
            () -> assertEquals(OUTDATE, underTest.getOutDate()),
            () -> assertEquals(OTHER_EXPBACKDATE, underTest.getExpBackDate()),
            () -> assertEquals(ACTBACKDATE, underTest.getActBackDate())
        );
    }

    @Test
    void testSetActBackDate() {
        //given
        underTest = defaultBuilder
            .build();

        //when
        underTest.setActBackDate(OTHER_ACTBACKDATE);

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(ISSUER, underTest.getIssuer()),
            () -> assertEquals(DESTINATION, underTest.getDestination()),
            () -> assertEquals(OUTDATE, underTest.getOutDate()),
            () -> assertEquals(EXPBACKDATE, underTest.getExpBackDate()),
            () -> assertEquals(OTHER_ACTBACKDATE, underTest.getActBackDate())
        );
    }

    @Test
    void testGetRentItemOfDevice() {
        //given
        underTest = defaultBuilder
            .build();
        underTest.getRentItems().add(rentItem);

        //when
        RentItem rentItemOfDevice = underTest.getRentItemOfScannable(device);

        //then
        assertEquals(rentItem, rentItemOfDevice);
    }

    @Test
    void testGetRentItemOfDeviceNotFound() {
        //given
        underTest = defaultBuilder
            .build();

        //when
        RentItem rentItemOfDevice = underTest.getRentItemOfScannable(device);

        //then
        assertNull(rentItemOfDevice);
    }

    private <T> void validationFails(final Set<ConstraintViolation<T>> violations, final String expectedMessage, final String expectedProperty) {
        assertThat(violations.size(), is(1));
        ConstraintViolation<T> violation = violations.stream().findFirst().get();
        assertThat(violation.getMessage(), is(expectedMessage));
        assertThat(violation.getPropertyPath().toString(), is(expectedProperty));
    }
}
