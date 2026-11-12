package interpreter.model.exp;

import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LogicExpTest {
    private final IDictionary<String, IValue> symTable = new MyDictionary<>();
    private static IExp booleanVal(boolean b) { return new ValueExp(new BoolValue(b)); }

    @Test
    void evaluatesLogicalAndCorrectly() throws MyException {
        IExp e = new LogicExp(LogicOp.AND, booleanVal(true), booleanVal(false));
        assertEquals(new BoolValue(false), e.eval(symTable));
    }

    @Test
    void evaluatesLogicalOrCorrectly() throws MyException {
        IExp e = new LogicExp(LogicOp.OR, booleanVal(true), booleanVal(false));
        assertEquals(new BoolValue(true), e.eval(symTable));
    }

    @Test
    void nonBooleanOperandThrowsException() {
        IExp invalidExp = new LogicExp(LogicOp.AND, booleanVal(true), new ValueExp(new IntValue(5)));
        assertThrows(ExpressionException.class, () -> invalidExp.eval(symTable));
    }
}