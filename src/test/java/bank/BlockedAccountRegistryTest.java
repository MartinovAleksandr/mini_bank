package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BlockedAccountRegistryTest {

    private static AccountNumber number(int value) {
        return new AccountNumber(String.format("%010d", value));
    }

    @Test
    void blockMakesAccountBlocked() {
        BlockedAccountRegistry registry = new BlockedAccountRegistry();

        registry.block(number(1));

        assertTrue(registry.isBlocked(number(1)));
    }

    @Test
    void blockingSameAccountTwiceDoesNotCreateDuplicate() {
        BlockedAccountRegistry registry = new BlockedAccountRegistry();
        AccountNumber number = number(1);

        registry.block(number);
        registry.block(number);

        assertEquals(1, registry.size());
    }

    @Test
    void unblockRemovesNumber() {
        BlockedAccountRegistry registry = new BlockedAccountRegistry();
        AccountNumber number = number(1);
        registry.block(number);

        registry.unblock(number);

        assertFalse(registry.isBlocked(number));
        assertEquals(0, registry.size());
    }

    @Test
    void unknownNumberIsNotBlocked() {
        BlockedAccountRegistry registry = new BlockedAccountRegistry();

        assertFalse(registry.isBlocked(number(1)));
    }
}
