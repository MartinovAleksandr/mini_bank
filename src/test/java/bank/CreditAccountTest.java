package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreditAccountTest {

    private static final double DELTA = 0.0001;

    @Test
    void accountCanGoNegative() {
        CreditAccount account = new CreditAccount("003", "Ivan", 1_000, 5_000);

        boolean result = account.withdraw(1_500);

        assertTrue(result);
        assertEquals(-500, account.getBalance(), DELTA);
    }

    @Test
    void creditLimitCanBeUsed() {
        CreditAccount account = new CreditAccount("003", "Ivan", 1_000, 5_000);

        boolean result = account.withdraw(6_000);

        assertTrue(result);
        assertEquals(-5_000, account.getBalance(), DELTA);
    }

    @Test
    void creditLimitCannotBeExceeded() {
        CreditAccount account = new CreditAccount("003", "Ivan", 1_000, 5_000);

        boolean result = account.withdraw(6_001);

        assertFalse(result);
        assertEquals(1_000, account.getBalance(), DELTA);
    }

    @Test
    void failedWithdrawalDoesNotChangeBalance() {
        CreditAccount account = new CreditAccount("003", "Ivan", 1_000, 5_000);

        account.withdraw(10_000);

        assertEquals(1_000, account.getBalance(), DELTA);
    }
}
