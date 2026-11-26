package interpreter.model.stmt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.MyException;
import interpreter.exceptions.StatementException;
import interpreter.model.PrgState;
import interpreter.model.exp.ArithExp;
import interpreter.model.exp.ArithOp;
import interpreter.model.exp.IExp;
import interpreter.model.exp.RelOp;
import interpreter.model.exp.RelationalExp;
import interpreter.model.exp.ValueExp;
import interpreter.model.exp.VarExp;
import interpreter.model.type.IntType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IntValue;
import java.util.List;
import org.junit.jupiter.api.Test;

class WhileStmtTest {
    private static IExp num(int n) { return new ValueExp(new IntValue(n)); }

    // int v; v = start; (while (v > 0) (print(v); v = v - 1))
    private static PrgState runCountdown(int start) throws MyException {
        IStmt program = new CompStmt(new VarDeclStmt("v", new IntType()),
                new CompStmt(new AssignStmt("v", num(start)),
                        new WhileStmt(new RelationalExp(RelOp.GT, new VarExp("v"), num(0)),
                                new CompStmt(new PrintStmt(new VarExp("v")),
                                        new AssignStmt("v",
                                                new ArithExp(ArithOp.SUB, new VarExp("v"), num(1)))))));
        PrgState state = PrgState.of(program);
        while (!state.getExeStack().isEmpty())
            state.getExeStack().pop().execute(state);
        return state;
    }

    @Test
    void loopRunsZeroTimes() throws MyException {
        assertTrue(runCountdown(0).getOut().toList().isEmpty());
    }

    @Test
    void loopRunsOnce() throws MyException {
        assertEquals(List.of(new IntValue(1)), runCountdown(1).getOut().toList());
    }

    @Test
    void loopRunsSeveralTimes() throws MyException {
        PrgState s = runCountdown(3);
        assertEquals(List.of(new IntValue(3), new IntValue(2), new IntValue(1)), s.getOut().toList());
        assertEquals(new IntValue(0), s.getSymTable().lookup("v"));
    }

    @Test
    void trueConditionPushesTheLoopThenTheBody() throws MyException {
        PrgState state = PrgState.of(new NopStmt());
        state.getExeStack().pop();
        IStmt body = new PrintStmt(num(1));
        var loop = new WhileStmt(new ValueExp(new BoolValue(true)), body);
        loop.execute(state);
        assertSame(body, state.getExeStack().pop());   // the body runs first
        assertSame(loop, state.getExeStack().pop());   // then the loop is tested again
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void falseConditionPushesNothing() throws MyException {
        PrgState state = PrgState.of(new NopStmt());
        state.getExeStack().pop();
        new WhileStmt(new ValueExp(new BoolValue(false)), new NopStmt()).execute(state);
        assertTrue(state.getExeStack().isEmpty());
    }

    @Test
    void nonBooleanConditionThrows() {
        PrgState state = PrgState.of(new NopStmt());
        var loop = new WhileStmt(num(1), new NopStmt());
        assertThrows(StatementException.class, () -> loop.execute(state));
    }

    @Test
    void toStringIsInfix() {
        var loop = new WhileStmt(new ValueExp(new BoolValue(true)), new NopStmt());
        assertEquals("(while (true) nop)", loop.toString());
    }
}