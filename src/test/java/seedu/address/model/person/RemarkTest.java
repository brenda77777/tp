package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class RemarkTest {

    @Test
    public void constructor_nullOrEmptyString_success() {
        assertEquals("", new Remark("").value);
        assertEquals("test remark", new Remark("test remark").value);
    }

    @Test
    public void equals() {
        Remark firstRemark = new Remark("hello");
        Remark secondRemark = new Remark("hello");
        Remark differentRemark = new Remark("bye");

        assertEquals(firstRemark, firstRemark);
        assertEquals(firstRemark, secondRemark);
        assertNotEquals(firstRemark, differentRemark);
        assertNotEquals(firstRemark, null);
        assertNotEquals(firstRemark, 1);
    }

    @Test
    public void toStringMethod() {
        Remark remark = new Remark("internship note");
        assertEquals("internship note", remark.toString());
    }
}
