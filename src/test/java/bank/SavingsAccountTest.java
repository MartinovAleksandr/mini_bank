package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SavingsAccountTest {

    private static final double DELTA = 0.0001;

    private static AccountNumber number(int value) {
        return new AccountNumber(String.format("%010d", value));
    }

    @Test
    void canWithdrawWhenMinimumBalanceIsPreserved() {
        SavingsAccount account = new SavingsAccount(number(2), "Ivan", 1_000, 200);

        boolean result = account.withdraw(700);

        assertTrue(result);
        assertEquals(300, account.getBalance(), DELTA);
    }

    @Test
    void cannotWithdrawBelowMinimumBalance() {
        SavingsAccount account = new SavingsAccount(number(2), "Ivan", 1_000, 200);

        boolean result = account.withdraw(900);

        assertFalse(result);
        assertEquals(1_000, account.getBalance(), DELTA);
    }

    @Test
    void failedWithdrawalDoesNotChangeBalance() {
        SavingsAccount account = new SavingsAccount(number(2), "Ivan", 1_000, 200);

        account.withdraw(5_000);

        assertEquals(1_000, account.getBalance(), DELTA);
    }
}
