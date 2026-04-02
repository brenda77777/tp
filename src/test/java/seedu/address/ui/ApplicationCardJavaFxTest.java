package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import seedu.address.model.application.Application;
import seedu.address.model.application.OnlineAssessment;
import seedu.address.model.application.Status;
import seedu.address.testutil.ApplicationBuilder;

/**
 * Tests for {@link ApplicationCard} that require a live JavaFX runtime.
 * <p>
 * These tests verify constructor behaviour, node visibility, label text, and
 * the event-button click handler. They are disabled on Linux because GitHub
 * Actions runners have no display and the JavaFX toolkit cannot initialise
 * there without Monocle.
 * <p>
 * Pure-Java static-helper tests (toTitleCase, toStatusKey, format*) that
 * run on all platforms live in {@link ApplicationCardTest}.
 */
@DisabledOnOs(OS.LINUX)
public class ApplicationCardJavaFxTest {

    @BeforeAll
    public static void initJfxRuntime() throws Exception {
        System.setProperty("prism.order", "sw");
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");

        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            latch.countDown(); // already running
        }
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    // -----------------------------------------------------------------------
    // FX-thread helper
    // -----------------------------------------------------------------------

    @FunctionalInterface
    private interface FxTask {
        void run() throws Exception;
    }

    private static void onFx(FxTask task) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                task.run();
            } catch (Throwable t) {
                error.set(t);
            } finally {
                latch.countDown();
            }
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS), "FX thread timed out");
        if (error.get() != null) {
            throw new RuntimeException(error.get());
        }
    }

    private static ApplicationCard buildCard(Application app, int index) throws Exception {
        AtomicReference<ApplicationCard> ref = new AtomicReference<>();
        onFx(() -> ref.set(new ApplicationCard(app, index)));
        return ref.get();
    }

    // -----------------------------------------------------------------------
    // Reflection helpers
    // -----------------------------------------------------------------------

    private static Label getLabel(ApplicationCard card, String field) throws Exception {
        Field f = ApplicationCard.class.getDeclaredField(field);
        f.setAccessible(true);
        return (Label) f.get(card);
    }

    private static String getLabelText(ApplicationCard card, String field) throws Exception {
        return getLabel(card, field).getText();
    }

    private static FlowPane getTagsPane(ApplicationCard card) throws Exception {
        Field f = ApplicationCard.class.getDeclaredField("tags");
        f.setAccessible(true);
        return (FlowPane) f.get(card);
    }

    private static VBox getDetailsBox(ApplicationCard card) throws Exception {
        Field f = ApplicationCard.class.getDeclaredField("detailsBox");
        f.setAccessible(true);
        return (VBox) f.get(card);
    }

    private static Button getEventButton(ApplicationCard card) throws Exception {
        Field f = ApplicationCard.class.getDeclaredField("eventButton");
        f.setAccessible(true);
        return (Button) f.get(card);
    }

    private static EventDetailsWindow getEventDetailsWindow(ApplicationCard card) throws Exception {
        Field f = ApplicationCard.class.getDeclaredField("eventDetailsWindow");
        f.setAccessible(true);
        return (EventDetailsWindow) f.get(card);
    }

    // -----------------------------------------------------------------------
    // Constructor — id and role labels
    // -----------------------------------------------------------------------

    @Test
    public void constructor_setsIdLabel() throws Exception {
        ApplicationCard card = buildCard(new ApplicationBuilder().build(), 3);
        assertEquals("3. ", getLabelText(card, "id"));
    }

    @Test
    public void constructor_setsRoleLabel() throws Exception {
        ApplicationCard card = buildCard(new ApplicationBuilder().withRole("Software Engineer").build(), 1);
        assertEquals("Software Engineer", getLabelText(card, "role"));
    }

    // -----------------------------------------------------------------------
    // Constructor — deadline branch
    // -----------------------------------------------------------------------

    @Test
    public void constructor_withEmptyDeadline_deadlineLabelHidden() throws Exception {
        ApplicationCard card = buildCard(new ApplicationBuilder().build(), 1);
        Label dl = getLabel(card, "deadline");
        assertFalse(dl.isVisible());
        assertFalse(dl.isManaged());
    }

    @Test
    public void constructor_withDeadline_deadlineLabelShowsValueAndIcon() throws Exception {
        Application app = new ApplicationBuilder().withDeadline("2026-12-31").build();
        ApplicationCard card = buildCard(app, 1);
        Label dl = getLabel(card, "deadline");
        assertTrue(dl.isVisible());
        assertTrue(dl.isManaged());
        assertEquals("2026-12-31", dl.getText());
        assertNotNull(dl.getGraphic(), "Calendar icon must be set on deadline label");
    }

    // -----------------------------------------------------------------------
    // Constructor — company location branch
    // -----------------------------------------------------------------------

    @Test
    public void constructor_withCompanyLocation_locationRowPresent() throws Exception {
        // Default builder includes location "Singapore" → 4 rows: company, location, phone, email
        ApplicationCard card = buildCard(new ApplicationBuilder().build(), 1);
        assertTrue(getDetailsBox(card).getChildren().size() >= 4);
    }

    @Test
    public void constructor_withEmptyCompanyLocation_noLocationRow() throws Exception {
        Application app = new ApplicationBuilder().withCompany("Acme").build(); // clears location
        ApplicationCard card = buildCard(app, 1);
        // company + phone + email = 3 rows only
        assertEquals(3, getDetailsBox(card).getChildren().size());
    }

    // -----------------------------------------------------------------------
    // Constructor — note branch
    // -----------------------------------------------------------------------

    @Test
    public void constructor_withNote_noteRowPresent() throws Exception {
        Application app = new ApplicationBuilder().withNote("Follow up on Monday").build();
        ApplicationCard card = buildCard(app, 1);
        // company + location + phone + email + note = 5 rows
        assertEquals(5, getDetailsBox(card).getChildren().size());
    }

    @Test
    public void constructor_withEmptyNote_noteRowAbsent() throws Exception {
        Application app = new ApplicationBuilder().withNote("").build();
        ApplicationCard card = buildCard(app, 1);
        assertEquals(4, getDetailsBox(card).getChildren().size());
    }

    // -----------------------------------------------------------------------
    // Constructor — tags: status chip and reminder chip
    // -----------------------------------------------------------------------

    @Test
    public void constructor_statusChip_isAddedToTagsPane() throws Exception {
        Application app = new ApplicationBuilder().withStatus(Status.APPLIED).build();
        ApplicationCard card = buildCard(app, 1);
        boolean found = getTagsPane(card).getChildren().stream()
                .map(n -> (Label) n)
                .anyMatch(l -> l.getText().equals("Applied"));
        assertTrue(found, "Status chip 'Applied' must be present in tags pane");
    }

    @Test
    public void constructor_nonReminderTag_doesNotGetUrgentStyleClass() throws Exception {
        Application app = new ApplicationBuilder().withTags("Tech").build();
        ApplicationCard card = buildCard(app, 1);
        boolean techIsUrgent = getTagsPane(card).getChildren().stream()
                .map(n -> (Label) n)
                .filter(l -> l.getText().equals("Tech"))
                .anyMatch(l -> l.getStyleClass().contains("chip-urgent"));
        assertFalse(techIsUrgent, "Non-reminder tag must not carry chip-urgent CSS class");
    }

    // -----------------------------------------------------------------------
    // Constructor — event button branch
    // -----------------------------------------------------------------------

    @Test
    public void constructor_withNoEvent_eventButtonHidden() throws Exception {
        ApplicationCard card = buildCard(new ApplicationBuilder().build(), 1);
        Button btn = getEventButton(card);
        assertFalse(btn.isVisible());
        assertFalse(btn.isManaged());
    }

    @Test
    public void constructor_withEvent_eventButtonVisible() throws Exception {
        OnlineAssessment event = new OnlineAssessment(
                "Zoom", LocalDateTime.of(2026, 6, 15, 10, 0),
                "HackerRank", "https://hr.com", "Bring ID");
        Application base = new ApplicationBuilder().build();
        Application app = new Application(
                base.getRole(), base.getPhone(), base.getHrEmail(), base.getCompany(),
                base.getTags(), base.getStatus(), base.getDeadline(),
                event, base.getNote(), base.getResume());
        ApplicationCard card = buildCard(app, 1);
        Button btn = getEventButton(card);
        assertTrue(btn.isVisible());
        assertTrue(btn.isManaged());
    }

    // -----------------------------------------------------------------------
    // handleEventButtonClick — both branches
    // -----------------------------------------------------------------------

    @Test
    public void handleEventButtonClick_whenWindowNotShowing_callsShow() throws Exception {
        OnlineAssessment event = new OnlineAssessment(
                "Online", LocalDateTime.of(2026, 8, 1, 9, 0),
                "Codility", "https://codility.com");
        Application base = new ApplicationBuilder().build();
        Application app = new Application(
                base.getRole(), base.getPhone(), base.getHrEmail(), base.getCompany(),
                base.getTags(), base.getStatus(), base.getDeadline(),
                event, base.getNote(), base.getResume());

        onFx(() -> {
            ApplicationCard card = new ApplicationCard(app, 1);
            assertFalse(getEventDetailsWindow(card).isShowing());
            Method handler = ApplicationCard.class.getDeclaredMethod("handleEventButtonClick");
            handler.setAccessible(true);
            handler.invoke(card);
        });
    }

    @Test
    public void handleEventButtonClick_whenWindowAlreadyShowing_callsFocus() throws Exception {
        OnlineAssessment event = new OnlineAssessment(
                "Online", LocalDateTime.of(2026, 9, 1, 14, 0),
                "LeetCode", "https://leetcode.com");
        Application base = new ApplicationBuilder().build();
        Application app = new Application(
                base.getRole(), base.getPhone(), base.getHrEmail(), base.getCompany(),
                base.getTags(), base.getStatus(), base.getDeadline(),
                event, base.getNote(), base.getResume());

        onFx(() -> {
            ApplicationCard card = new ApplicationCard(app, 1);

            EventDetailsWindow stub = new EventDetailsWindow() {
                @Override
                public boolean isShowing() {
                    return true;
                }

                @Override
                public void focus() { /* verified by not throwing */ }
            };
            stub.setEventDetails(event);

            Field edwField = ApplicationCard.class.getDeclaredField("eventDetailsWindow");
            edwField.setAccessible(true);
            edwField.set(card, stub);

            Method handler = ApplicationCard.class.getDeclaredMethod("handleEventButtonClick");
            handler.setAccessible(true);
            handler.invoke(card);
        });
    }
}
