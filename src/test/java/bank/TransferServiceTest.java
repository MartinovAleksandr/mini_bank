package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransferServiceTest {

    private static final double DELTA = 0.0001;

    @Test
    void transferWithoutCommission() {
        DebitAccount from = new DebitAccount("1", "Alice", 10_000);
        DebitAccount to = new DebitAccount("2", "Bob", 0);

        boolean result = new TransferService(new NoCommission()).transfer(from, to, 5_000);

        assertTrue(result);
        assertEquals(5_000, from.getBalance(), DELTA);
        assertEquals(5_000, to.getBalance(), DELTA);
    }

    @Test
    void transferWithPercentCommission() {
        DebitAccount from = new DebitAccount("1", "Alice", 20_000);
        DebitAccount to = new DebitAccount("2", "Bob", 0);

        boolean result = new TransferService(new PercentCommission(1)).transfer(from, to, 10_000);

        assertTrue(result);
        assertEquals(9_900, from.getBalance(), DELTA);
        assertEquals(10_000, to.getBalance(), DELTA);
    }

    @Test
    void transferRejectedWhenCommissionCannotBePaid() {
        DebitAccount from = new DebitAccount("1", "Alice", 10_050);
        DebitAccount to = new DebitAccount("2", "Bob", 0);

        boolean result = new TransferService(new PercentCommission(1)).transfer(from, to, 10_000);

        assertFalse(result);
        assertEquals(10_050, from.getBalance(), DELTA);
        assertEquals(0, to.getBalance(), DELTA);
    }

    @Test
    void transferToSameAccountRejected() {
        DebitAccount from = new DebitAccount("1", "Alice", 10_000);

        boolean result = new TransferService(new NoCommission()).transfer(from, from, 1_000);

        assertFalse(result);
        assertEquals(10_000, from.getBalance(), DELTA);
    }
}
