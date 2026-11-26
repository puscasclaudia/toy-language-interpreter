package interpreter.model.adt;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.exceptions.ADTException;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MyHeapTest {
    private IHeap heap;

    @BeforeEach
    void setUp() { heap = new MyHeap(); }

    @Test
    void allocateReturnsConsecutiveAddressesStartingAtOne() {
        assertEquals(1, heap.allocate(new IntValue(10)));
        assertEquals(2, heap.allocate(new IntValue(20)));
        assertEquals(3, heap.allocate(new IntValue(30)));
    }

    @Test
    void getReturnsTheStoredValue() throws ADTException {
        int a = heap.allocate(new IntValue(10));
        assertEquals(new IntValue(10), heap.get(a));
    }

    @Test
    void getOfAMissingAddressThrows() {
        var ex = assertThrows(ADTException.class, () -> heap.get(1));
        assertEquals("address 1 is not in the heap", ex.getMessage());
    }

    @Test
    void updateReplacesTheValue() throws ADTException {
        int a = heap.allocate(new IntValue(10));
        heap.update(a, new IntValue(99));
        assertEquals(new IntValue(99), heap.get(a));
    }

    @Test
    void updateOfAMissingAddressThrows() {
        assertThrows(ADTException.class, () -> heap.update(5, new IntValue(1)));
    }

    @Test
    void isDefinedReflectsAllocation() {
        assertFalse(heap.isDefined(1));
        heap.allocate(new IntValue(10));
        assertTrue(heap.isDefined(1));
        assertFalse(heap.isDefined(0));   // 0 is null, never allocated
    }

    @Test
    void getContentIsAReadOnlyCopy() {
        heap.allocate(new IntValue(10));
        Map<Integer, IValue> copy = heap.getContent();
        assertThrows(UnsupportedOperationException.class, () -> copy.put(9, new IntValue(1)));
        heap.allocate(new IntValue(20));
        assertEquals(1, copy.size());
    }

    @Test
    void setContentReplacesTheContent() {
        heap.allocate(new IntValue(10));
        heap.allocate(new IntValue(20));
        heap.setContent(Map.of(2, new IntValue(20)));
        assertFalse(heap.isDefined(1));
        assertTrue(heap.isDefined(2));
    }

    @Test
    void addressesAreNotReusedAfterSetContent() {
        heap.allocate(new IntValue(10));
        heap.allocate(new IntValue(20));
        heap.setContent(Map.of());   // everything collected
        assertEquals(3, heap.allocate(new IntValue(30)));
    }
}
