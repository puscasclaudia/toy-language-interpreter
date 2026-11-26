package interpreter.controller;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.exceptions.RepositoryException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NopStmt;
import interpreter.model.value.IntValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ControllerTest {
    @TempDir
    Path dir;

    private PrgState run(IStmt program) throws MyException {
        PrgState state = PrgState.of(program);
        var repo = new Repository(state, dir.resolve("log.txt").toString());
        new Controller(repo, false).allStep();   // display off
        return state;
    }

    @Test
    void example1PrintsTwo() throws MyException {
        PrgState s = run(Examples.EX1);
        assertTrue(s.getExeStack().isEmpty());
        assertEquals(new IntValue(2), s.getOut().get(0));
        assertEquals(new IntValue(2), s.getSymTable().lookup("v"));
    }

    @Test
    void example2PrintsEighteen() throws MyException {
        PrgState s = run(Examples.EX2);
        assertTrue(s.getExeStack().isEmpty());
        assertEquals(1, s.getOut().size());
        assertEquals(new IntValue(18), s.getOut().get(0));
    }

    @Test
    void example3PrintsTwo() throws MyException {
        PrgState s = run(Examples.EX3);
        assertEquals(new IntValue(2), s.getOut().get(0));
    }

    @Test
    void example5PrintsFive() throws MyException {
        PrgState s = run(Examples.EX5);
        assertEquals(1, s.getOut().size());
        assertEquals(new IntValue(5), s.getOut().get(0));
    }

    @Test
    void fileExampleReadsBothNumbersAndClosesTheFile() throws Exception {
        Path in = dir.resolve("test.in");
        Files.writeString(in, "15\n50\n");
        PrgState s = run(Examples.fileExample(in.toString()));
        assertEquals(new IntValue(15), s.getOut().get(0));
        assertEquals(new IntValue(50), s.getOut().get(1));
        assertTrue(s.getFileTable().toMap().isEmpty());
    }

    @Test
    void fileExampleWithAnEmptyFilePrintsZeroZero() throws Exception {
        Path in = dir.resolve("empty.in");
        Files.writeString(in, "");
        PrgState s = run(Examples.fileExample(in.toString()));
        assertEquals(new IntValue(0), s.getOut().get(0));
        assertEquals(new IntValue(0), s.getOut().get(1));
    }

    @Test
    void oneStepOnEmptyStackThrows() throws MyException {
        PrgState state = PrgState.of(new NopStmt());
        var controller = new Controller(new Repository(state, dir.resolve("log.txt").toString()), false);
        controller.oneStep(state);   // executes nop, the stack is now empty
        assertThrows(StatementException.class, () -> controller.oneStep(state));
    }

    @Test
    void oneStepExecutesExactlyOneStatement() throws MyException {
        PrgState state = PrgState.of(Examples.EX1);
        var controller = new Controller(new Repository(state, dir.resolve("log.txt").toString()), false);
        controller.oneStep(state);   // S1 : (int v ; rest)
        assertEquals(2, state.getExeStack().toList().size());
        assertTrue(state.getSymTable().toMap().isEmpty());
    }

    @Test
    void runtimeErrorsAreReportedAsMyException() {
        IStmt bad = new interpreter.model.stmt.PrintStmt(new interpreter.model.exp.VarExp("zzz"));
        assertThrows(MyException.class, () -> run(bad));
    }

    @Test
    void logFileContainsTheFourSectionsInOrder() throws Exception {
        run(Examples.EX1);
        String log = Files.readString(dir.resolve("log.txt"));
        int a = log.indexOf("ExeStack:"), b = log.indexOf("SymTable:"),
                c = log.indexOf("Out:"), d = log.indexOf("FileTable:");
        assertTrue(0 <= a && a < b && b < c && c < d);
    }

    @Test
    void logFileHasOneBlockPerStepPlusTheInitialState() throws Exception {
        run(Examples.EX1);   // 5 steps (see section 3.3) + initial state = 6 blocks
        String log = Files.readString(dir.resolve("log.txt"));
        assertEquals(6, log.split("ExeStack:", -1).length - 1);
    }

    @Test
    void unwritableLogFileThrowsRepositoryException() {
        PrgState state = PrgState.of(new NopStmt());
        var repo = new Repository(state, dir.resolve("no/such/dir/log.txt").toString());
        assertThrows(RepositoryException.class, () -> new Controller(repo, false).allStep());
    }
}
