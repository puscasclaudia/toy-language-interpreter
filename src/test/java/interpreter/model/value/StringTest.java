package interpreter.model.value;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.model.PrgState;
import interpreter.model.stmt.NopStmt;
import interpreter.model.stmt.VarDeclStmt;
import interpreter.model.type.StringType;
import org.junit.jupiter.api.Test;

class StringTest {
    @Test
    void stringVariableGetsEmptyStringByDefault() throws Exception {
        PrgState state = PrgState.of(new NopStmt());
        new VarDeclStmt("s", new StringType()).execute(state);
        assertEquals(new StringValue(""), state.getSymTable().lookup("s"));
    }

    @Test
    void equalStringValuesAreEqualWithSameHashCode() {
        var a = new StringValue("test.in");
        var b = new StringValue("test.in");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void differentStringValuesAreNotEqual() {
        assertNotEquals(new StringValue("a"), new StringValue("b"));
    }

    @Test
    void valuesKnowTheirTypes() {
        assertEquals(new StringType(), new StringValue("a").getType());
        assertEquals(new interpreter.model.type.IntType(), new IntValue(1).getType());
        assertEquals(new interpreter.model.type.BoolType(), new BoolValue(true).getType());
    }
}

