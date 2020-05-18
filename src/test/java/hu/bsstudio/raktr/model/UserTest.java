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

public class UserTest {
    private static final Validator VALIDATOR = javax.validation.Validation.buildDefaultValidatorFactory().getValidator();


    private static final String ROLE_NAME = "RoleName";
    private static final String USERNAME = "username";
    private static final String NICKNAME = "nickname";
    private static final String FAMILY_NAME = "familyName";
    private static final String GIVEN_NAME = "givenName";
    private static final String PERSONAL_ID = "personalId";
    private static final long ID = 1L;

    private User underTest;

    private UserRole userRole = UserRole.builder()
        .withRoleName(ROLE_NAME)
        .build();

    private User.Builder defaultBuilder = User.builder()
        .withId(ID)
        .withUsername(USERNAME)
        .withNickName(NICKNAME)
        .withFamilyName(FAMILY_NAME)
        .withGivenName(GIVEN_NAME)
        .withPersonalId(PERSONAL_ID);

    @Test
    void testConstructorAndGetters() {
        //given
        underTest = defaultBuilder.build();

        //when

        //then
        assertAll(
            () -> assertEquals(ID, underTest.getId()),
            () -> assertEquals(USERNAME, underTest.getUsername()),
            () -> assertEquals(NICKNAME, underTest.getNickName()),
            () -> assertEquals(FAMILY_NAME, underTest.getFamilyName()),
            () -> assertEquals(GIVEN_NAME, underTest.getGivenName()),
            () -> assertEquals(PERSONAL_ID, underTest.getPersonalId())
        );
    }

    @Test
    void testNoArgsConstructor() {
        //given
        underTest = new User();

        //when

        //then
        assertAll(
            () -> assertNull(underTest.getId()),
            () -> assertNull(underTest.getUsername()),
            () -> assertNull(underTest.getNickName()),
            () -> assertNull(underTest.getFamilyName()),
            () -> assertNull(underTest.getGivenName()),
            () -> assertNull(underTest.getPersonalId())
        );
    }

    @Test
    void testAddRole() {
        //given
        underTest = defaultBuilder.build();

        //when
        underTest.addRole(userRole);

        //then
        assertEquals(1, underTest.getRoles().size());
    }

    @Test
    void testValidationShouldFailForBlankUsername() {
        //given
        underTest = defaultBuilder
            .withUsername("")
            .build();

        //when
        Set<ConstraintViolation<User>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be blank", "username");
    }

    @Test
    void testValidationShouldFailForNullFamilyName() {
        //given
        underTest = defaultBuilder
            .withFamilyName(null)
            .build();

        //when
        Set<ConstraintViolation<User>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be null", "familyName");
    }

    @Test
    void testValidationShouldFailForNullGivenName() {
        //given
        underTest = defaultBuilder
            .withGivenName(null)
            .build();

        //when
        Set<ConstraintViolation<User>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be null", "givenName");
    }

    @Test
    void testValidationShouldFailForNullPersonalId() {
        //given
        underTest = defaultBuilder
            .withPersonalId(null)
            .build();

        //when
        Set<ConstraintViolation<User>> violations = VALIDATOR.validate(underTest);

        //then
        validationFails(violations, "must not be null", "personalId");
    }


    private <T> void validationFails(final Set<ConstraintViolation<T>> violations, final String expectedMessage, final String expectedProperty) {
        assertThat(violations.size(), is(1));
        ConstraintViolation<T> violation = violations.stream().findFirst().get();
        assertThat(violation.getMessage(), is(expectedMessage));
        assertThat(violation.getPropertyPath().toString(), is(expectedProperty));
    }
}
