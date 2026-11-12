package interpreter.model.adt;

import interpreter.exceptions.ADTException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ADTTest {

    @Test
    void testStackOperationsAndDeepCopy() throws ADTException {
        IStack<Integer> stack = new MyStack<>();
        stack.push(1);
        stack.push(2);

        IStack<Integer> copy = stack.deepCopy();
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
        assertTrue(stack.isEmpty());

        assertFalse(copy.isEmpty());
        assertEquals(2, copy.pop());
    }

    @Test
    void testStackEmptyPopThrowsException() {
        IStack<String> stack = new MyStack<>();
        assertThrows(ADTException.class, stack::pop);
    }

    @Test
    void testDictionaryOperations() throws ADTException {
        IDictionary<String, Integer> dict = new MyDictionary<>();
        dict.put("x", 10);
        assertTrue(dict.isDefined("x"));
        assertEquals(10, dict.lookup("x"));

        dict.update("x", 20);
        assertEquals(20, dict.lookup("x"));

        assertFalse(dict.isDefined("y"));
        assertThrows(ADTException.class, () -> dict.lookup("y"));
    }
}
