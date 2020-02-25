package hu.bsstudio.raktr.model;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class LocationTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private static final String NAME = "location";
    private static final String NAME2 = "location2";

    private Location underTest;
/*
    @Test
    void testConstructorAndGetters() {
        //given

        //when
        underTest = Location.builder()
                .withName(NAME)
                .build();

        //then
        assertAll(
                () -> assertEquals(NAME, underTest.getName()),
                () -> assertNull(underTest.getDevices())
        );
    }*/

    @Test
    void noArgsConstructor() {
        //given

        //when
        underTest = new Location();

        //then
        assertNull(underTest.getId());
        assertNull(underTest.getName());
    }

    @Test
    void testValidationShouldFailForMissingName() {
        //given
        underTest = Location.builder()
                .build();

        //when
        final Set<ConstraintViolation<Location>> violations = VALIDATOR.validate(underTest);

        //then
        assertThat(violations.size(), is(1));
        ConstraintViolation<Location> violation = violations.stream().findFirst().get();
        assertThat(violation.getMessage(), is("must not be blank"));
        assertThat(violation.getPropertyPath().toString(), is("name"));
    }

    @Test
    void testSetName(){
        //given
        underTest = Location.builder()
                .withName(NAME)
                .build();

        //when
        underTest.setName(NAME2);

        //then
        assertEquals(NAME2, underTest.getName());
    }
}
