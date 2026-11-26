package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.IExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.BoolType;
import interpreter.model.type.IntType;
import interpreter.model.value.IntValue;
import interpreter.model.value.StringValue;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.BufferedReader;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
class FileStmtTest {
    @TempDir
    Path dir;

    private PrgState state;
    private ValueExp file;
    private StringValue fileName;

    @BeforeEach
    void setUp() throws Exception {
        Path f = dir.resolve("numbers.in");
        Files.writeString(f, "7\n");
        fileName = new StringValue(f.toString());
        file = new ValueExp(fileName);
        state = PrgState.of(new NopStmt());
        new VarDeclStmt("x", new IntType()).execute(state);
    }

    @AfterEach
    void closeOpenFiles() throws IOException {
        // release file handles so @TempDir can delete the folder (needed on Windows)
        for (BufferedReader reader : state.getFileTable().toMap().values()) {
            reader.close();
        }
    }
    // S8
    @Test
    void openAddsTheFileToTheFileTable() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertTrue(state.getFileTable().isDefined(fileName));
    }

    @Test
    void openWithNonStringExpressionThrows() {
        assertThrows(StatementException.class,
                () -> new OpenRFileStmt(new ValueExp(new IntValue(1))).execute(state));
    }

    @Test
    void openingTheSameFileTwiceThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class, () -> new OpenRFileStmt(file).execute(state));
    }

    @Test
    void openingAMissingFileThrows() {
        IExp missing = new ValueExp(new StringValue(dir.resolve("nope.in").toString()));
        assertThrows(StatementException.class, () -> new OpenRFileStmt(missing).execute(state));
    }

    // S9
    @Test
    void readGivesTheNextNumberThenZeroAtTheEnd() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(7), state.getSymTable().lookup("x"));
        new ReadFileStmt(file, "x").execute(state);
        assertEquals(new IntValue(0), state.getSymTable().lookup("x"));
    }

    @Test
    void readReadsNumbersInOrder() throws Exception {
        Path f = dir.resolve("two.in");
        Files.writeString(f, "15\n50\n");
        IExp e = new ValueExp(new StringValue(f.toString()));
        new OpenRFileStmt(e).execute(state);
        new ReadFileStmt(e, "x").execute(state);
        assertEquals(new IntValue(15), state.getSymTable().lookup("x"));
        new ReadFileStmt(e, "x").execute(state);
        assertEquals(new IntValue(50), state.getSymTable().lookup("x"));
    }

    @Test
    void readIntoUndeclaredVariableThrows() throws MyException {
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "nope").execute(state));
    }

    @Test
    void readIntoNonIntVariableThrows() throws MyException {
        new VarDeclStmt("b", new BoolType()).execute(state);
        new OpenRFileStmt(file).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "b").execute(state));
    }

    @Test
    void readWithNonStringExpressionThrows() {
        assertThrows(StatementException.class,
                () -> new ReadFileStmt(new ValueExp(new IntValue(1)), "x").execute(state));
    }

    @Test
    void readFromAFileThatIsNotOpenThrows() {
        assertThrows(StatementException.class, () -> new ReadFileStmt(file, "x").execute(state));
    }

    @Test
    void readOfANonIntegerLineThrows() throws Exception {
        Path f = dir.resolve("bad.in");
        Files.writeString(f, "abc\n");
        IExp e = new ValueExp(new StringValue(f.toString()));
        new OpenRFileStmt(e).execute(state);
        assertThrows(StatementException.class, () -> new ReadFileStmt(e, "x").execute(state));
    }

    @Test
    void readFromAnEmptyFileGivesZero() throws Exception {
        Path f = dir.resolve("empty.in");
        Files.writeString(f, "");
        IExp e = new ValueExp(new StringValue(f.toString()));
        new OpenRFileStmt(e).execute(state);
        new ReadFileStmt(e, "x").execute(state);
        assertEquals(new IntValue(0), state.getSymTable().lookup("x"));
    }

    // S10
    @Test
    void closeRemovesTheFileFromTheFileTable() throws MyException {
        new OpenRFileStmt(file).execute(state);
        new CloseRFileStmt(file).execute(state);
        assertFalse(state.getFileTable().isDefined(fileName));
    }

    @Test
    void closingAFileThatIsNotOpenThrows() {
        assertThrows(StatementException.class, () -> new CloseRFileStmt(file).execute(state));
    }

    @Test
    void closeWithNonStringExpressionThrows() {
        assertThrows(StatementException.class,
                () -> new CloseRFileStmt(new ValueExp(new IntValue(1))).execute(state));
    }

    @Test
    void fileNameCanComeFromAVariable() throws MyException {
        new VarDeclStmt("f", new interpreter.model.type.StringType()).execute(state);
        new AssignStmt("f", file).execute(state);
        new OpenRFileStmt(new VarExp("f")).execute(state);
        assertTrue(state.getFileTable().isDefined(fileName));
    }
}

