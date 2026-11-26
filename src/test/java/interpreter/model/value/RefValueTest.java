package interpreter.model.value;

import static org.junit.jupiter.api.Assertions.*;

import interpreter.model.type.IntType;
import interpreter.model.type.RefType;
import org.junit.jupiter.api.Test;

class RefValueTest {
    @Test
    void typeIsRefOfTheLocationType() {
        assertEquals(new RefType(new IntType()), new RefValue(1, new IntType()).getType());
    }

    @Test
    void referenceToAReferenceHasTypeRefRefInt() {
        var v = new RefValue(10, new RefType(new IntType()));
        assertEquals(new RefType(new RefType(new IntType())), v.getType());
    }

    @Test
    void valuesWithTheSameAddressAndTypeAreEqual() {
        assertEquals(new RefValue(1, new IntType()), new RefValue(1, new IntType()));
        assertEquals(new RefValue(1, new IntType()).hashCode(),
                new RefValue(1, new IntType()).hashCode());
    }

    @Test
    void valuesWithADifferentAddressAreNotEqual() {
        assertNotEquals(new RefValue(1, new IntType()), new RefValue(2, new IntType()));
    }

    @Test
    void toStringMatchesTheExamplesOfTheAssignment() {
        assertEquals("(1, int)", new RefValue(1, new IntType()).toString());
        assertEquals("(2, Ref(int))", new RefValue(2, new RefType(new IntType())).toString());
    }
}