package hu.bsstudio.raktr.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;

public class CategoryTest {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private static final String NAME = "category";
    private static final String NAME2 = "category2";

    private Category underTest;

    @Test
    void testConstructorAndGetters() {
        //given

        //when
        underTest = Category.builder()
            .withName(NAME)
            .build();

        //then
        assertAll(
            () -> assertEquals(NAME, underTest.getName()),
            () -> assertNull(underTest.getDevices())
        );
    }

    @Test
    void testNoArgsConstructor() {
        //given

        //when
        underTest = new Category();

        //then
        assertNull(underTest.getId());
        assertNull(underTest.getName());
    }

    @Test
    void testValidationShouldFailForMissingName() {
        //given
        underTest = Category.builder()
            .build();

        //when
        Set<ConstraintViolation<Category>> violations = VALIDATOR.validate(underTest);

        //then
        assertThat(violations.size(), is(1));
        ConstraintViolation<Category> violation = violations.stream().findFirst().get();
        assertThat(violation.getMessage(), is("must not be blank"));
        assertThat(violation.getPropertyPath().toString(), is("name"));
    }

    @Test
    void testSetName() {
        //given
        underTest = Category.builder()
            .withName(NAME)
            .build();

        //when
        underTest.setName(NAME2);

        //then
        assertEquals(NAME2, underTest.getName());
    }
}
