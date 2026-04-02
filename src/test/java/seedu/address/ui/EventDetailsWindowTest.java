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
import javafx.scene.control.Label;
import javafx.stage.Stage;
import seedu.address.model.application.ApplicationEvent;
import seedu.address.model.application.OnlineAssessment;

/**
 * Tests for {@link EventDetailsWindow}.
 * <p>
 * Every method in EventDetailsWindow operates on JavaFX nodes loaded from FXML
 * or on a live {@link Stage}, so the entire class requires the JavaFX toolkit.
 * It is therefore disabled on Linux CI runners that have no display.
 */
@DisabledOnOs(OS.LINUX)
public class EventDetailsWindowTest {

    @BeforeAll
    public static void initJfxRuntime() throws Exception {
        System.setProperty("prism.order", "sw");
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");

        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            latch.countDown();
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

    private static Label getLabel(EventDetailsWindow w, String fieldName) throws Exception {
        Field f = EventDetailsWindow.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        return (javafx.scene.control.Label) f.get(w);
    }

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    @Test
    public void constructor_noArg_createsWindow() throws Exception {
        AtomicReference<EventDetailsWindow> ref = new AtomicReference<>();
        onFx(() -> ref.set(new EventDetailsWindow()));
        assertNotNull(ref.get());
        assertNotNull(ref.get().getRoot());
    }

    @Test
    public void constructor_withStage_usesProvidedStage() throws Exception {
        AtomicReference<Stage> stageRef = new AtomicReference<>();
        AtomicReference<EventDetailsWindow> windowRef = new AtomicReference<>();
        onFx(() -> {
            Stage stage = new Stage();
            stageRef.set(stage);
            windowRef.set(new EventDetailsWindow(stage));
        });
        assertEquals(stageRef.get(), windowRef.get().getRoot());
    }

    // -----------------------------------------------------------------------
    // setEventDetails — OnlineAssessment branch
    // -----------------------------------------------------------------------

    @Test
    public void setEventDetails_onlineAssessment_setsAllLabels() throws Exception {
        OnlineAssessment oa = new OnlineAssessment(
                "Zoom", LocalDateTime.of(2026, 6, 15, 10, 0),
                "HackerRank", "https://hr.com", "Bring your ID");

        AtomicReference<EventDetailsWindow> ref = new AtomicReference<>();
        onFx(() -> {
            EventDetailsWindow w = new EventDetailsWindow();
            w.setEventDetails(oa);
            ref.set(w);
        });

        EventDetailsWindow w = ref.get();
        assertEquals("Online Assessment Details", getLabel(w, "titleLabel").getText());
        assertEquals("Zoom", getLabel(w, "locationLabel").getText());
        assertEquals("15 Jun 2026, 10:00", getLabel(w, "dateTimeLabel").getText());
        assertEquals("HackerRank", getLabel(w, "platformLabel").getText());
        assertEquals("https://hr.com", getLabel(w, "linkLabel").getText());
        assertEquals("Bring your ID", getLabel(w, "notesLabel").getText());
    }

    @Test
    public void setEventDetails_onlineAssessmentNoNotes_usesDefaultNotesValue() throws Exception {
        OnlineAssessment oa = new OnlineAssessment(
                "Teams", LocalDateTime.of(2026, 7, 20, 14, 30),
                "Codility", "https://codility.com"); // 4-arg: notes = EMPTY_NOTES_VALUE

        AtomicReference<EventDetailsWindow> ref = new AtomicReference<>();
        onFx(() -> {
            EventDetailsWindow w = new EventDetailsWindow();
            w.setEventDetails(oa);
            ref.set(w);
        });

        assertEquals(OnlineAssessment.EMPTY_NOTES_VALUE,
                getLabel(ref.get(), "notesLabel").getText());
    }

    // -----------------------------------------------------------------------
    // setEventDetails — generic ApplicationEvent fallback branch (else path)
    // -----------------------------------------------------------------------

    @Test
    public void setEventDetails_genericEvent_usesFallbackLabels() throws Exception {
        // Anonymous subclass — not an OnlineAssessment → triggers the else branch
        ApplicationEvent generic = new ApplicationEvent(
                "Conference Room A", LocalDateTime.of(2026, 9, 1, 9, 0)) {
        };

        AtomicReference<EventDetailsWindow> ref = new AtomicReference<>();
        onFx(() -> {
            EventDetailsWindow w = new EventDetailsWindow();
            w.setEventDetails(generic);
            ref.set(w);
        });

        EventDetailsWindow w = ref.get();
        assertEquals("Event Details", getLabel(w, "titleLabel").getText());
        assertEquals("Conference Room A", getLabel(w, "locationLabel").getText());
        assertEquals("01 Sep 2026, 09:00", getLabel(w, "dateTimeLabel").getText());
        assertEquals("N/A", getLabel(w, "platformLabel").getText());
        assertEquals("N/A", getLabel(w, "linkLabel").getText());
        assertEquals("N/A", getLabel(w, "notesLabel").getText());
    }

    // -----------------------------------------------------------------------
    // isShowing, show, hide
    // -----------------------------------------------------------------------

    @Test
    public void isShowing_beforeShow_returnsFalse() throws Exception {
        AtomicReference<Boolean> result = new AtomicReference<>();
        onFx(() -> result.set(new EventDetailsWindow().isShowing()));
        assertFalse(result.get());
    }

    @Test
    public void show_thenHide_windowIsHiddenAfterwards() throws Exception {
        AtomicReference<Boolean> showingAfterHide = new AtomicReference<>();
        onFx(() -> {
            EventDetailsWindow w = new EventDetailsWindow();
            w.show();
            assertTrue(w.isShowing());
            w.hide();
            showingAfterHide.set(w.isShowing());
        });
        assertFalse(showingAfterHide.get());
    }

    @Test
    public void hide_whenNotShowing_doesNotThrow() throws Exception {
        onFx(() -> new EventDetailsWindow().hide());
    }

    // -----------------------------------------------------------------------
    // focus
    // -----------------------------------------------------------------------

    @Test
    public void focus_doesNotThrow() throws Exception {
        onFx(() -> new EventDetailsWindow().focus());
    }

    // -----------------------------------------------------------------------
    // handleClose (private @FXML) — exercises the hide() delegation
    // -----------------------------------------------------------------------

    @Test
    public void handleClose_hidesWindow() throws Exception {
        AtomicReference<Boolean> showingAfterClose = new AtomicReference<>();
        onFx(() -> {
            EventDetailsWindow w = new EventDetailsWindow();
            w.show();
            assertTrue(w.isShowing());
            Method handleClose = EventDetailsWindow.class.getDeclaredMethod("handleClose");
            handleClose.setAccessible(true);
            handleClose.invoke(w);
            showingAfterClose.set(w.isShowing());
        });
        assertFalse(showingAfterClose.get());
    }

    // -----------------------------------------------------------------------
    // setEventDetails called twice — labels reflect latest event
    // -----------------------------------------------------------------------

    @Test
    public void setEventDetails_calledTwice_labelsReflectLatestEvent() throws Exception {
        OnlineAssessment first = new OnlineAssessment(
                "Zoom", LocalDateTime.of(2026, 5, 1, 9, 0),
                "HackerRank", "https://hr.com", "First");
        OnlineAssessment second = new OnlineAssessment(
                "Teams", LocalDateTime.of(2026, 8, 15, 14, 0),
                "Codility", "https://codility.com", "Second");

        AtomicReference<EventDetailsWindow> ref = new AtomicReference<>();
        onFx(() -> {
            EventDetailsWindow w = new EventDetailsWindow();
            w.setEventDetails(first);
            w.setEventDetails(second);
            ref.set(w);
        });

        EventDetailsWindow w = ref.get();
        assertEquals("Teams", getLabel(w, "locationLabel").getText());
        assertEquals("Second", getLabel(w, "notesLabel").getText());
    }
}
