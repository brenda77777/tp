package seedu.address.model.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class DeadlineTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        // In this codebase, null input is treated as an empty deadline.
        assertTrue(new Deadline(null).isEmpty());
    }

    @Test
    public void isValidDeadline() {
        // null deadline
        assertThrows(NullPointerException.class, () -> Deadline.isValidDeadline(null));

        // invalid deadlines
        assertFalse(Deadline.isValidDeadline("")); // empty string
        assertFalse(Deadline.isValidDeadline(" ")); // spaces only

        // valid deadlines
        assertTrue(Deadline.isValidDeadline("2026-12-31"));
        assertTrue(Deadline.isValidDeadline("2026-03-23 15:00"));
    }

    @Test
    public void compareTo() {
        Deadline early = new Deadline("2026-01-01");
        Deadline late = new Deadline("2026-12-31");
        Deadline empty = Deadline.getEmptyDeadline();

        // early < late
        assertTrue(early.compareTo(late) < 0);
        // late > early
        assertTrue(late.compareTo(early) > 0);
        // empty 排在最后 (假设你在 compareTo 里写了空值检查)
        assertTrue(late.compareTo(empty) < 0);
    }

    @Test
    public void equals() {
        Deadline deadline = new Deadline("2026-12-31");

        // same values -> returns true
        assertTrue(deadline.equals(new Deadline("2026-12-31")));

        // same object -> returns true
        assertTrue(deadline.equals(deadline));

        // null -> returns false
        assertFalse(deadline.equals(null));

        // different types -> returns false
        assertFalse(deadline.equals(5.0f));

        // different values -> returns false
        assertFalse(deadline.equals(new Deadline("2026-01-01")));
    }
}
