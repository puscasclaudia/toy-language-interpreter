package interpreter.model;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.model.stmt.NopStmt;
import interpreter.model.type.IntType;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PrgStateTest {
    @Test
    void newStateHasAnEmptyHeap() {
        assertTrue(PrgState.of(new NopStmt()).getHeap().getContent().isEmpty());
    }

    @Test
    void logHasTheHeapSectionAfterTheFileTable() {
        String log = PrgState.of(new NopStmt()).toLogString();
        assertTrue(log.indexOf("FileTable:") < log.indexOf("Heap:"));
    }

    @Test
    void logListsTheHeapOneLinePerLocation() {
        PrgState state = PrgState.of(new NopStmt());
        state.getHeap().allocate(new IntValue(20));
        state.getHeap().allocate(new RefValue(1, new IntType()));
        assertTrue(state.toLogString().endsWith("Heap:\n1 --> 20\n2 --> (1, int)\n"));
    }

    @Test
    void logSortsTheHeapAddressesNumerically() {
        PrgState state = PrgState.of(new NopStmt());
        state.getHeap().setContent(Map.of(10, new IntValue(1), 2, new IntValue(2)));
        String log = state.toLogString();
        assertTrue(log.indexOf("\n2 -->") < log.indexOf("\n10 -->"));
    }
}
