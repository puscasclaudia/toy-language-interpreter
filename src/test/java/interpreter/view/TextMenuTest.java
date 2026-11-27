package interpreter.view;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TextMenuTest {
    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream captured;
    private List<String> executed;
    private TextMenu menu;

    private Command recording(String key, String description) {
        return new Command(key, description) {
            @Override public void execute() { executed.add(key); }
        };
    }

    private void typed(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    @BeforeEach
    void setUp() {
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        executed = new ArrayList<>();
        menu = new TextMenu();
        menu.addCommand(recording("1", "first option"));
        menu.addCommand(recording("2", "second option"));
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
    }

    @Test
    void menuPrintsEveryCommandWithItsDescriptionInOrder() {
        typed("");   // end of input right away
        menu.show();
        String out = captured.toString();
        assertTrue(out.contains("1 : first option"));
        assertTrue(out.indexOf("first option") < out.indexOf("second option"));
    }

    @Test
    void typedKeyExecutesTheMatchingCommand() {
        typed("2\n1\n");
        menu.show();
        assertEquals(List.of("2", "1"), executed);
    }

    @Test
    void unknownOptionIsRejectedAndTheMenuContinues() {
        typed("9\n1\n");
        menu.show();
        assertTrue(captured.toString().contains("Invalid option"));
        assertEquals(List.of("1"), executed);
    }

    @Test
    void surroundingSpacesInTheInputAreIgnored() {
        typed("  1  \n");
        menu.show();
        assertEquals(List.of("1"), executed);
    }

    @Test
    void menuEndsWhenTheInputEnds() {
        typed("1\n");
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(5), menu::show);
    }
}