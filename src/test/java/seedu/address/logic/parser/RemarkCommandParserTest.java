package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.model.person.Remark;

public class RemarkCommandParserTest {

    private RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_validArgs_success() {
        assertParseSuccess(parser, "1 r/test",
                new RemarkCommand(Index.fromOneBased(1), new Remark("test")));

        assertParseSuccess(parser, "2 r/hello world",
                new RemarkCommand(Index.fromOneBased(2), new Remark("hello world")));
    }

    @Test
    public void parse_emptyRemarkWithPrefix_success() {
        assertParseSuccess(parser, "1 r/",
                new RemarkCommand(Index.fromOneBased(1), new Remark("")));
    }

    @Test
    public void parse_missingRemarkPrefix_success() {
        assertParseSuccess(parser, "1",
                new RemarkCommand(Index.fromOneBased(1), new Remark("")));
    }

    @Test
    public void parse_invalidIndex_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "0 r/test", expectedMessage);
        assertParseFailure(parser, "-1 r/test", expectedMessage);
        assertParseFailure(parser, "abc r/test", expectedMessage);
    }

    @Test
    public void parse_missingIndex_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);

        assertParseFailure(parser, "r/test", expectedMessage);
        assertParseFailure(parser, "", expectedMessage);
    }
}

