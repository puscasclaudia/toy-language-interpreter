package interpreter.model.exp;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ExpressionException;
import interpreter.exceptions.MyException;
import interpreter.model.adt.IDictionary;
import interpreter.model.adt.MyDictionary;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import interpreter.model.adt.IHeap;
import interpreter.model.adt.MyHeap;

class VarAndValueExpTest {
    private IDictionary<String, IValue> symTable;
    private final IHeap heap = new MyHeap();

    @BeforeEach
    void setUp() { symTable = new MyDictionary<>(); }

    @Test
    void valueExpReturnsItsValue() throws MyException {
        assertEquals(new IntValue(4), new ValueExp(new IntValue(4)).eval(symTable,heap));
    }

    @Test
    void varExpLooksTheVariableUp() throws MyException {
        symTable.put("x", new IntValue(9));
        assertEquals(new IntValue(9), new VarExp("x").eval(symTable,heap));
    }

    @Test
    void undefinedVariableThrows() {
        var ex = assertThrows(ExpressionException.class, () -> new VarExp("x").eval(symTable,heap));
        assertEquals("variable x is not defined", ex.getMessage());
    }
}
