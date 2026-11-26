package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HeapStmtTest {
    private PrgState state;

    private static ValueExp num(int n) { return new ValueExp(new IntValue(n)); }

    @BeforeEach
    void setUp() throws MyException {
        state = PrgState.of(new NopStmt());
        new VarDeclStmt("v", new RefType(new IntType())).execute(state);   // Ref int v
    }

    // S11
    @Test
    void newAllocatesAtAddressOneAndUpdatesTheVariable() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        assertEquals(new RefValue(1, new IntType()), state.getSymTable().lookup("v"));
        assertEquals(new IntValue(20), state.getHeap().get(1));
    }

    @Test
    void secondNewGetsTheNextAddressAndLeavesTheFirstLocation() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        new NewStmt("v", num(30)).execute(state);
        assertEquals(new RefValue(2, new IntType()), state.getSymTable().lookup("v"));
        assertEquals(new IntValue(20), state.getHeap().get(1));
        assertEquals(new IntValue(30), state.getHeap().get(2));
    }

    @Test
    void newCanStoreAReference() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        new VarDeclStmt("a", new RefType(new RefType(new IntType()))).execute(state);
        new NewStmt("a", new VarExp("v")).execute(state);
        assertEquals(new RefValue(2, new RefType(new IntType())), state.getSymTable().lookup("a"));
        assertEquals(new RefValue(1, new IntType()), state.getHeap().get(2));
    }

    @Test
    void newOfAnUndeclaredVariableThrows() {
        assertThrows(StatementException.class, () -> new NewStmt("zzz", num(1)).execute(state));
    }

    @Test
    void newOnANonReferenceVariableThrows() throws MyException {
        new VarDeclStmt("n", new IntType()).execute(state);
        assertThrows(StatementException.class, () -> new NewStmt("n", num(1)).execute(state));
    }

    @Test
    void newWithAValueOfTheWrongTypeThrows() {
        var exp = new ValueExp(new BoolValue(true));
        assertThrows(StatementException.class, () -> new NewStmt("v", exp).execute(state));
        assertTrue(state.getHeap().getContent().isEmpty());   // nothing was allocated
    }

    // S12
    @Test
    void writeChangesTheHeapLocation() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        new WriteHeapStmt("v", num(30)).execute(state);
        assertEquals(new IntValue(30), state.getHeap().get(1));
    }

    @Test
    void writeDoesNotChangeTheVariable() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        new WriteHeapStmt("v", num(30)).execute(state);
        assertEquals(new RefValue(1, new IntType()), state.getSymTable().lookup("v"));
    }

    @Test
    void writeThroughAnUndeclaredVariableThrows() {
        assertThrows(StatementException.class, () -> new WriteHeapStmt("zzz", num(1)).execute(state));
    }

    @Test
    void writeThroughANonReferenceThrows() throws MyException {
        new VarDeclStmt("n", new IntType()).execute(state);
        assertThrows(StatementException.class, () -> new WriteHeapStmt("n", num(1)).execute(state));
    }

    @Test
    void writeToAnAddressNotInTheHeapThrows() {
        // v still holds the null reference (0, int)
        assertThrows(StatementException.class, () -> new WriteHeapStmt("v", num(1)).execute(state));
    }

    @Test
    void writeOfAValueOfTheWrongTypeThrows() throws MyException {
        new NewStmt("v", num(20)).execute(state);
        var exp = new ValueExp(new BoolValue(true));
        assertThrows(StatementException.class, () -> new WriteHeapStmt("v", exp).execute(state));
        assertEquals(new IntValue(20), state.getHeap().get(1));   // unchanged
    }
}
