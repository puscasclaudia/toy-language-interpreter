package interpreter.model.exp;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.IHeap;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyHeap;
import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReadHeapExpTest {
    private IDictionary<String, IValue> symTable;
    private IHeap heap;

    // the state of Ex7: v -> (1, int), a -> (2, Ref(int)), heap {1 -> 20, 2 -> (1, int)}
    @BeforeEach
    void setUp() {
        symTable = new MyDictionary<>();
        heap = new MyHeap();
        heap.allocate(new IntValue(20));
        heap.allocate(new RefValue(1, new IntType()));
        symTable.put("v", new RefValue(1, new IntType()));
        symTable.put("a", new RefValue(2, new RefType(new IntType())));
        symTable.put("n", new IntValue(5));
        symTable.put("null", new RefValue(0, new IntType()));
    }

    @Test
    void readsTheStoredValue() throws MyException {
        assertEquals(new IntValue(20), new ReadHeapExp(new VarExp("v")).eval(symTable, heap));
    }

    @Test
    void readsThroughTwoReferences() throws MyException {
        IExp e = new ReadHeapExp(new ReadHeapExp(new VarExp("a")));
        assertEquals(new IntValue(20), e.eval(symTable, heap));
    }

    @Test
    void canBeUsedInsideArithmetic() throws MyException {
        IExp e = new ArithExp(ArithOp.ADD,
                new ReadHeapExp(new ReadHeapExp(new VarExp("a"))),
                new ValueExp(new IntValue(5)));
        assertEquals(new IntValue(25), e.eval(symTable, heap));
    }

    @Test
    void nonReferenceExpressionThrows() {
        assertThrows(ExpressionException.class,
                () -> new ReadHeapExp(new VarExp("n")).eval(symTable, heap));
    }

    @Test
    void addressNotInTheHeapThrows() {
        symTable.put("v", new RefValue(9, new IntType()));
        var ex = assertThrows(ExpressionException.class,
                () -> new ReadHeapExp(new VarExp("v")).eval(symTable, heap));
        assertEquals("rH: address 9 is not in the heap", ex.getMessage());
    }

    @Test
    void nullReferenceThrows() {
        assertThrows(ExpressionException.class,
                () -> new ReadHeapExp(new VarExp("null")).eval(symTable, heap));
    }

    @Test
    void toStringShowsTheNesting() {
        assertEquals("rH(rH(a))", new ReadHeapExp(new ReadHeapExp(new VarExp("a"))).toString());
    }
}
