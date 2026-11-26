package interpreter.model.exp;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.model.adt.IHeap;
import interpreter.model.adt.MyDictionary;
import interpreter.model.adt.MyHeap;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class HeapPassingTest {
    private final IHeap heap = new MyHeap();
    private final AtomicInteger calls = new AtomicInteger();

    private IExp intProbe() {
        return (symTable, h) -> { assertSame(heap, h); calls.incrementAndGet(); return new IntValue(1); };
    }

    private IExp boolProbe() {
        return (symTable, h) -> { assertSame(heap, h); calls.incrementAndGet(); return new BoolValue(true); };
    }

    @Test
    void arithExpPassesTheHeapToBothOperands() throws MyException {
        new ArithExp(ArithOp.ADD, intProbe(), intProbe()).eval(new MyDictionary<>(), heap);
        assertEquals(2, calls.get());
    }

    @Test
    void relationalExpPassesTheHeapToBothOperands() throws MyException {
        new RelationalExp(RelOp.LT, intProbe(), intProbe()).eval(new MyDictionary<>(), heap);
        assertEquals(2, calls.get());
    }

    @Test
    void logicExpPassesTheHeapToBothOperands() throws MyException {
        new LogicExp(LogicOp.AND, boolProbe(), boolProbe()).eval(new MyDictionary<>(), heap);
        assertEquals(2, calls.get());
    }
}
