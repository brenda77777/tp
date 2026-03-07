package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.Remark;

public class RemarkCommandTest {

    @Test
    public void equals() {
        RemarkCommand firstCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("hello"));
        RemarkCommand secondCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("hello"));
        RemarkCommand differentCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark("bye"));

        assertEquals(firstCommand, firstCommand);
        assertEquals(firstCommand, secondCommand);
        assertNotEquals(firstCommand, differentCommand);
        assertNotEquals(firstCommand, null);
        assertNotEquals(firstCommand, 1);
    }
}
