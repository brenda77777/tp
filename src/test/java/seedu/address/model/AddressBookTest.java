package seedu.company.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.company.logic.commands.CommandTestUtil.VALID_COMPANY_BOB;
import static seedu.company.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.company.testutil.Assert.assertThrows;
import static seedu.company.testutil.TypicalApplications.ALICE;
import static seedu.company.testutil.TypicalApplications.getTypicalCompanyBook;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.company.model.application.Application;
import seedu.company.model.application.exceptions.DuplicateApplicationException;
import seedu.company.testutil.ApplicationBuilder;

public class CompanyBookTest {

    private final CompanyBook companyBook = new CompanyBook();

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), companyBook.getApplicationList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> companyBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyCompanyBook_replacesData() {
        CompanyBook newData = getTypicalCompanyBook();
        companyBook.resetData(newData);
        assertEquals(newData, companyBook);
    }

    @Test
    public void resetData_withDuplicateApplications_throwsDuplicateApplicationException() {
        // Two applications with the same identity fields
        Application editedAlice = new ApplicationBuilder(ALICE).withCompany(VALID_COMPANY_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Application> newApplications = Arrays.asList(ALICE, editedAlice);
        CompanyBookStub newData = new CompanyBookStub(newApplications);

        assertThrows(DuplicateApplicationException.class, () -> companyBook.resetData(newData));
    }

    @Test
    public void hasApplication_nullApplication_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> companyBook.hasApplication(null));
    }

    @Test
    public void hasApplication_applicationNotInCompanyBook_returnsFalse() {
        assertFalse(companyBook.hasApplication(ALICE));
    }

    @Test
    public void hasApplication_applicationInCompanyBook_returnsTrue() {
        companyBook.addApplication(ALICE);
        assertTrue(companyBook.hasApplication(ALICE));
    }

    @Test
    public void hasApplication_applicationWithSameIdentityFieldsInCompanyBook_returnsTrue() {
        companyBook.addApplication(ALICE);
        Application editedAlice = new ApplicationBuilder(ALICE).withCompany(VALID_COMPANY_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(companyBook.hasApplication(editedAlice));
    }

    @Test
    public void getApplicationList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> companyBook.getApplicationList().remove(0));
    }

    @Test
    public void toStringMethod() {
        String expected = CompanyBook.class.getCanonicalRole() + "{applications=" + companyBook.getApplicationList() + "}";
        assertEquals(expected, companyBook.toString());
    }

    /**
     * A stub ReadOnlyCompanyBook whose applications list can violate interface constraints.
     */
    private static class CompanyBookStub implements ReadOnlyCompanyBook {
        private final ObservableList<Application> applications = FXCollections.observableArrayList();

        CompanyBookStub(Collection<Application> applications) {
            this.applications.setAll(applications);
        }

        @Override
        public ObservableList<Application> getApplicationList() {
            return applications;
        }
    }

}
