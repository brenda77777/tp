package seedu.company.testutil;

import seedu.company.model.CompanyBook;
import seedu.company.model.application.Application;

/**
 * A utility class to help with building Companybook objects.
 * Example usage: <br>
 *     {@code CompanyBook ab = new CompanyBookBuilder().withApplication("John", "Doe").build();}
 */
public class CompanyBookBuilder {

    private CompanyBook companyBook;

    public CompanyBookBuilder() {
        companyBook = new CompanyBook();
    }

    public CompanyBookBuilder(CompanyBook companyBook) {
        this.companyBook = companyBook;
    }

    /**
     * Adds a new {@code Application} to the {@code CompanyBook} that we are building.
     */
    public CompanyBookBuilder withApplication(Application application) {
        companyBook.addApplication(application);
        return this;
    }

    public CompanyBook build() {
        return companyBook;
    }
}
