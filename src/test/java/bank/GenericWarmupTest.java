package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenericWarmupTest {

    @Test
    void boxStoresString() {
        Box<String> text = new Box<>();
        text.set("Java");

        String value = text.get();

        assertEquals("Java", value);
    }

    @Test
    void boxStoresInteger() {
        Box<Integer> number = new Box<>();
        number.set(42);

        Integer value = number.get();

        assertEquals(42, value);
    }

    @Test
    void pairSupportsDifferentTypeCombinations() {
        Pair<String, Integer> age = new Pair<>("Ivan", 20);
        Pair<AccountNumber, String> owner = new Pair<>(new AccountNumber("1234567890"), "Ivan");

        assertEquals("Ivan", age.key());
        assertEquals(20, age.value());
        assertEquals(new AccountNumber("1234567890"), owner.key());
        assertEquals("Ivan", owner.value());
    }

    @Test
    void genericMethodsWorkWithArrays() {
        String[] values = {"Java", "Kotlin", "Scala"};

        assertEquals("Java", GenericUtils.first(values));
        assertEquals("Scala", GenericUtils.last(values));
        assertTrue(GenericUtils.contains(values, "Kotlin"));
        assertFalse(GenericUtils.contains(values, "Python"));
    }
}
