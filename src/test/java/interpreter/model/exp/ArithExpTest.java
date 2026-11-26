package interpreter.model.exp;

import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.value.IntValue;
import interpreter.model.value.IValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import interpreter.model.adt.IHeap;
import interpreter.model.adt.MyHeap;
class ArithExpTest {
    private final IDictionary<String, IValue> symTable = new MyDictionary<>();
    private final IHeap heap = new MyHeap();
    private static IExp num(int n) { return new ValueExp(new IntValue(n)); }

    @Test
    void evaluatesAdditionCorrectly() throws MyException {
        IExp e = new ArithExp(ArithOp.ADD, num(5), num(3));
        assertEquals(new IntValue(8), e.eval(symTable,heap));
    }

    @Test
    void respectsOperatorPrecedenceInSyntaxTree() throws MyException {
        IExp e = new ArithExp(ArithOp.ADD, num(2),
                new ArithExp(ArithOp.MUL, num(3), num(5)));
        assertEquals(new IntValue(17), e.eval(symTable,heap));
    }

    @Test
    void divisionByZeroThrowsException() {
        IExp e = new ArithExp(ArithOp.DIV, num(10), num(0));
        var ex = assertThrows(ExpressionException.class, () -> e.eval(symTable,heap));
        assertEquals("division by zero", ex.getMessage());
    }

    @Test
    void nonIntegerOperandThrowsException() {
        IExp invalidExp = new ArithExp(ArithOp.ADD, num(5), new ValueExp(new interpreter.model.value.BoolValue(true)));
        assertThrows(ExpressionException.class, () -> invalidExp.eval(symTable,heap));
    }
}
