package interpreter.controller;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import interpreter.model.value.BoolValue;
import interpreter.model.value.IValue;
import interpreter.model.value.IntValue;
import interpreter.model.value.RefValue;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class GarbageCollectorTest {
    // the state of Ex9 after new(v, 30): v -> (3, int), a -> (2, Ref(int))
    private final List<IValue> symTable = List.of(
            new RefValue(3, new IntType()), new RefValue(2, new RefType(new IntType())));
    private final Map<Integer, IValue> heap = Map.of(
            1, new IntValue(20), 2, new RefValue(1, new IntType()),
            3, new IntValue(30), 4, new IntValue(7));

    @Test
    void addressesOfTheSymTableIgnoreNonReferences() {
        List<IValue> values = List.of(new IntValue(1), new RefValue(5, new IntType()), new BoolValue(true));
        assertEquals(Set.of(5), GarbageCollector.getAddrFromSymTable(values));
    }

    @Test
    void unsafeCollectorLosesALocationReachableOnlyThroughTheHeap() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);
        var kept = GarbageCollector.unsafeGarbageCollector(roots, heap);
        assertEquals(Set.of(2, 3), kept.keySet());   // 1 was wrongly removed
    }

    @Test
    void safeCollectorKeepsTheLocationsReachableThroughTheHeap() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);
        Map<Integer, IValue> kept = GarbageCollector.safeGarbageCollector(roots, heap);
        assertEquals(Set.of(1, 2, 3), kept.keySet());   // 4 is garbage, 1 is not
    }

    @Test
    void safeCollectorKeepsTheValuesNotJustTheKeys() {
        Set<Integer> roots = GarbageCollector.getAddrFromSymTable(symTable);
        var kept = GarbageCollector.safeGarbageCollector(roots, heap);
        assertEquals(new IntValue(20), kept.get(1));
        assertEquals(new RefValue(1, new IntType()), kept.get(2));
    }

    @Test
    void unreachableLocationsAreRemoved() {
        var kept = GarbageCollector.safeGarbageCollector(Set.of(3), heap);
        assertEquals(Set.of(3), kept.keySet());
    }

    @Test
    void noRootsMeansAnEmptyHeap() {
        assertTrue(GarbageCollector.safeGarbageCollector(Set.of(), heap).isEmpty());
    }

    @Test
    void reachableAddressesFollowsAChainOfReferences() {
        Map<Integer, IValue> chain = Map.of(
                1, new RefValue(2, new IntType()),
                2, new RefValue(3, new IntType()),
                3, new IntValue(5),
                4, new IntValue(9));
        assertEquals(Set.of(1, 2, 3), GarbageCollector.reachableAddresses(Set.of(1), chain));
    }

    @Test
    void reachableAddressesTerminatesOnCycles() {
        Map<Integer, IValue> cycle = Map.of(
                1, new RefValue(2, new IntType()),
                2, new RefValue(1, new IntType()));
        assertEquals(Set.of(1, 2), GarbageCollector.reachableAddresses(Set.of(1), cycle));
    }

    @Test
    void nullAndDanglingRootsAreIgnored() {
        assertTrue(GarbageCollector.reachableAddresses(Set.of(0, 99), heap).isEmpty());
    }
}