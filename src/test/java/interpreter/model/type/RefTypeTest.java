package interpreter.model.type;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.model.PrgState;
import interpreter.model.stmt.NopStmt;
import interpreter.model.stmt.VarDeclStmt;
import interpreter.model.value.RefValue;
import org.junit.jupiter.api.Test;

class RefTypeTest {
    @Test
    void refVariableGetsTheNullReferenceByDefault() throws Exception {
        PrgState state = PrgState.of(new NopStmt());
        new VarDeclStmt("x", new RefType(new IntType())).execute(state);
        assertEquals(new RefValue(0, new IntType()), state.getSymTable().lookup("x"));
    }

    @Test
    void nestedRefVariableGetsTheNullReferenceToTheInnerRefType() throws Exception {
        PrgState state = PrgState.of(new NopStmt());
        new VarDeclStmt("x", new RefType(new RefType(new IntType()))).execute(state);
        assertEquals(new RefValue(0, new RefType(new IntType())), state.getSymTable().lookup("x"));
    }

    @Test
    void nestedRefTypesAreComparedByContent() {
        var a = new RefType(new RefType(new IntType()));
        var b = new RefType(new RefType(new IntType()));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void refRefIntDiffersFromRefInt() {
        assertNotEquals(new RefType(new RefType(new IntType())), new RefType(new IntType()));
    }

    @Test
    void refIntDiffersFromRefBool() {
        assertNotEquals(new RefType(new IntType()), new RefType(new BoolType()));
    }

    @Test
    void toStringShowsTheNesting() {
        assertEquals("Ref(Ref(int))", new RefType(new RefType(new IntType())).toString());
    }
}
