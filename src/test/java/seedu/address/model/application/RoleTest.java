package seedu.company.model.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.company.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class RoleTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Role(null));
    }

    @Test
    public void constructor_invalidRole_throwsIllegalArgumentException() {
        String invalidRole = "";
        assertThrows(IllegalArgumentException.class, () -> new Role(invalidRole));
    }

    @Test
    public void isValidRole() {
        // null role
        assertThrows(NullPointerException.class, () -> seedu.company.model.application.Role.isValidRole(null));

        // invalid role
        assertFalse(seedu.company.model.application.Role.isValidRole("")); // empty string
        assertFalse(seedu.company.model.application.Role.isValidRole(" ")); // spaces only
        assertFalse(seedu.company.model.application.Role.isValidRole("^")); // only non-alphanumeric characters
        assertFalse(seedu.company.model.application.Role.isValidRole("peter*")); // contains non-alphanumeric characters

        // valid role
        assertTrue(seedu.company.model.application.Role.isValidRole("peter jack")); // alphabets only
        assertTrue(seedu.company.model.application.Role.isValidRole("12345")); // numbers only
        assertTrue(seedu.company.model.application.Role.isValidRole("peter the 2nd")); // alphanumeric characters
        assertTrue(seedu.company.model.application.Role.isValidRole("Capital Tan")); // with capital letters
        assertTrue(seedu.company.model.application.Role.isValidRole("David Roger Jackson Ray Jr 2nd")); // long roles
    }

    @Test
    public void equals() {
        Role role = new Role("Valid Role");

        // same values -> returns true
        assertTrue(role.equals(new Role("Valid Role")));

        // same object -> returns true
        assertTrue(role.equals(role));

        // null -> returns false
        assertFalse(role.equals(null));

        // different types -> returns false
        assertFalse(role.equals(5.0f));

        // different values -> returns false
        assertFalse(role.equals(new Role("Other Valid Role")));
    }
}
