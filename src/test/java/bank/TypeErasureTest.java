package bank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class TypeErasureTest {

    @Test
    void genericTypeInformationIsErasedAtRuntime() {
        List<String> strings = new ArrayList<>();
        List<Integer> numbers = new ArrayList<>();

        assertSame(strings.getClass(), numbers.getClass());
        assertEquals(ArrayList.class, strings.getClass());
        assertEquals(ArrayList.class, numbers.getClass());
    }
}
