package interpreter.view;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ExamplesTest {
    @Test
    void example1IsPrintedInInfixForm() {
        assertEquals("(int v; (v = 2; print(v)))", Examples.EX1.toString());
    }

    @Test
    void example2KeepsTheOperatorPrecedenceInTheTree() {
        assertEquals("(int a; (int b; (a = (2 + (3 * 5)); (b = (a + 1); print(b)))))",
                Examples.EX2.toString());
    }

    @Test
    void example3ShowsTheIfStatement() {
        assertTrue(Examples.EX3.toString().contains("(if (a) then (v = 2) else (v = 3))"));
    }

    @Test
    void fileExampleUsesTheGivenFileName() {
        assertTrue(Examples.fileExample("data.txt").toString().contains("varf = \"data.txt\""));
    }

    @Test
    void example4ReadsTestIn() {
        assertTrue(Examples.EX4.toString().contains("varf = \"test.in\""));
    }

    @Test
    void example5UsesARelationalCondition() {
        assertTrue(Examples.EX5.toString().contains("(if ((a < b)) then (print(a)) else (print(b)))"));
    }

    @Test
    void example6To9UseReferenceTypes() {
        assertTrue(Examples.EX6.toString().startsWith("(Ref(int) v; (new(v, 20);"));
        assertTrue(Examples.EX7.toString().contains("rH(rH(a))"));
        assertTrue(Examples.EX8.toString().contains("wH(v, 30)"));
        assertTrue(Examples.EX9.toString().contains("new(v, 30)"));
    }

    @Test
    void example10ContainsTheWhileLoop() {
        assertTrue(Examples.EX10.toString().contains("(while ((v > 0))"));
    }
}
