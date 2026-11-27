package interpreter.view;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.controller.Controller;
import interpreter.model.PrgState;
import interpreter.model.exp.VarExp;
import interpreter.model.stmt.PrintStmt;
import interpreter.model.value.IntValue;
import interpreter.repository.Repository;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RunExampleCommandTest {
    @TempDir
    Path dir;

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream captured;
    private Path logFile;

    @BeforeEach
    void setUp() {
        captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        logFile = dir.resolve("log.txt");
    }

    @AfterEach
    void tearDown() { System.setOut(originalOut); }

    private RunExampleCommand commandFor(PrgState state) {
        var controller = new Controller(new Repository(state, logFile.toString()), false);
        return new RunExampleCommand("1", "an example", controller);
    }

    @Test
    void keyAndDescriptionAreStored() {
        var cmd = commandFor(PrgState.of(Examples.EX1));
        assertEquals("1", cmd.getKey());
        assertEquals("an example", cmd.getDescription());
    }

    @Test
    void executeRunsTheProgramToTheEnd() {
        PrgState state = PrgState.of(Examples.EX2);
        commandFor(state).execute();
        assertTrue(state.getExeStack().isEmpty());
        assertEquals(new IntValue(18), state.getOut().toList().getFirst());
    }

    @Test
    void anExampleRunsOnlyOnce() throws Exception {
        PrgState state = PrgState.of(Examples.EX1);
        var cmd = commandFor(state);
        cmd.execute();
        String logAfterFirstRun = Files.readString(logFile);
        cmd.execute();
        assertTrue(captured.toString().contains("This example has already been executed."));
        assertEquals(logAfterFirstRun, Files.readString(logFile));   // nothing more was run
    }

    @Test
    void anInterpreterErrorIsShownNotThrown() {
        var cmd = commandFor(PrgState.of(new PrintStmt(new VarExp("zzz"))));
        assertDoesNotThrow(cmd::execute);
        assertTrue(captured.toString().contains("Error: variable zzz is not defined"));
    }
}