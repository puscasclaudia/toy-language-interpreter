package interpreter.repository;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.exceptions.RepositoryException;
import interpreter.model.PrgState;
import interpreter.model.stmt.NopStmt;
import interpreter.model.value.IntValue;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class RepositoryTest {
    @TempDir
    Path dir;

    private PrgState state;
    private Path logFile;
    private IRepository repo;

    @BeforeEach
    void setUp() {
        state = PrgState.of(new NopStmt());
        logFile = dir.resolve("log.txt");
        repo = new Repository(state, logFile.toString());
    }

    @Test
    void getCrtPrgReturnsTheInitialState() {
        assertSame(state, repo.getCrtPrg());
    }

    @Test
    void loggingWritesTheFiveSectionsInOrder() throws Exception {
        repo.logPrgStateExec();
        String log = Files.readString(logFile);
        int a = log.indexOf("ExeStack:"), b = log.indexOf("SymTable:"), c = log.indexOf("Out:"),
                d = log.indexOf("FileTable:"), e = log.indexOf("Heap:");
        assertTrue(0 <= a && a < b && b < c && c < d && d < e);
    }

    @Test
    void loggingShowsTheCurrentContent() throws Exception {
        state.getOut().add(new IntValue(42));
        repo.logPrgStateExec();
        assertTrue(Files.readString(logFile).contains("Out:\n42\n"));
    }

    @Test
    void everyCallAppendsAnotherBlock() throws Exception {
        repo.logPrgStateExec();
        repo.logPrgStateExec();
        String log = Files.readString(logFile);
        assertEquals(2, log.split("ExeStack:", -1).length - 1);
    }

    @Test
    void loggingKeepsTheExistingContentOfTheFile() throws Exception {
        Files.writeString(logFile, "old line\n");
        repo.logPrgStateExec();
        assertTrue(Files.readString(logFile).startsWith("old line\n"));
    }

    @Test
    void loggingToAnUnwritablePathThrowsRepositoryException() {
        IRepository bad = new Repository(state, dir.resolve("no/such/dir/log.txt").toString());
        var ex = assertThrows(RepositoryException.class, bad::logPrgStateExec);
        assertTrue(ex.getMessage().startsWith("cannot write the log file"));
    }

    @Test
    void repositoryExceptionIsAMyException() {
        IRepository bad = new Repository(state, dir.resolve("no/such/dir/log.txt").toString());
        assertThrows(MyException.class, bad::logPrgStateExec);
    }
}
