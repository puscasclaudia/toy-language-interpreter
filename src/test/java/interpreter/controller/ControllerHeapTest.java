package interpreter.controller;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.stmt.CompStmt;
import interpreter.model.stmt.IStmt;
import interpreter.model.stmt.NewStmt;
import interpreter.model.stmt.VarDeclStmt;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import interpreter.repository.Repository;
import interpreter.view.Examples;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ControllerHeapTest {
    @TempDir
    Path dir;

    private static final RefType REF_INT = new RefType(new IntType());

    private PrgState run(IStmt program) throws MyException {
        PrgState state = PrgState.of(program);
        var repo = new Repository(state, dir.resolve("log.txt").toString());
        new Controller(repo, false).allStep();
        return state;
    }

    @Test
    void example6() throws MyException {
        PrgState s = run(Examples.EX6);
        assertEquals(Map.<Integer, IValue>of(1, new IntValue(20), 2, new RefValue(1, new IntType())),
                s.getHeap().getContent());
        assertEquals(new RefValue(1, new IntType()), s.getSymTable().lookup("v"));
        assertEquals(new RefValue(2, REF_INT), s.getSymTable().lookup("a"));
        assertEquals(List.of(new RefValue(1, new IntType()), new RefValue(2, REF_INT)),
                s.getOut().toList());
    }

    @Test
    void example7() throws MyException {
        PrgState s = run(Examples.EX7);
        assertEquals(List.of(new IntValue(20), new IntValue(25)), s.getOut().toList());
        assertEquals(2, s.getHeap().getContent().size());
    }

    @Test
    void example8() throws MyException {
        PrgState s = run(Examples.EX8);
        assertEquals(Map.<Integer, IValue>of(1, new IntValue(30)), s.getHeap().getContent());
        assertEquals(List.of(new IntValue(20), new IntValue(35)), s.getOut().toList());
    }

    @Test
    void example9PrintsTwentyWithTheSafeCollector() throws MyException {
        PrgState s = run(Examples.EX9);
        assertEquals(List.of(new IntValue(20)), s.getOut().toList());
        assertEquals(3, s.getHeap().getContent().size());   // location 1 survived
    }

    @Test
    void example10CountsDown() throws MyException {
        PrgState s = run(Examples.EX10);
        assertEquals(List.of(new IntValue(4), new IntValue(3), new IntValue(2),
                new IntValue(1), new IntValue(0)), s.getOut().toList());
    }

    @Test
    void unreachableLocationIsRemovedAfterTheStepThatDroppedIt() throws Exception {
        // Ref int v; new(v, 20); new(v, 30)  -> location 1 is garbage afterwards
        IStmt p = new CompStmt(new VarDeclStmt("v", REF_INT),
                new CompStmt(new NewStmt("v", new ValueExp(new IntValue(20))),
                        new NewStmt("v", new ValueExp(new IntValue(30)))));
        PrgState s = run(p);
        assertEquals(Map.<Integer, IValue>of(2, new IntValue(30)), s.getHeap().getContent());
        String log = Files.readString(dir.resolve("log.txt"));
        assertTrue(log.contains("1 --> 20\n2 --> 30\n"));   // logged before the collection
    }

    @Test
    void logHasTheHeapSectionAfterTheFileTable() throws Exception {
        run(Examples.EX6);
        String log = Files.readString(dir.resolve("log.txt"));
        assertTrue(log.indexOf("FileTable:") < log.indexOf("Heap:"));
    }
}