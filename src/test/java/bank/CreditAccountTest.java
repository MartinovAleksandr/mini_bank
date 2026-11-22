package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreditAccountTest {

    private static final double DELTA = 0.0001;

    private static AccountNumber number(int value) {
        return new AccountNumber(String.format("%010d", value));
    }

    @Test
    void accountCanGoNegative() {
        CreditAccount account = new CreditAccount(number(3), "Ivan", 1_000, 5_000);

        account.withdraw(1_500);

        assertEquals(-500, account.getBalance(), DELTA);
    }

    @Test
    void creditLimitCanBeUsed() {
        CreditAccount account = new CreditAccount(number(3), "Ivan", 1_000, 5_000);

        account.withdraw(6_000);

        assertEquals(-5_000, account.getBalance(), DELTA);
    }

    @Test
    void creditLimitCannotBeExceeded() {
        CreditAccount account = new CreditAccount(number(3), "Ivan", 1_000, 5_000);

        assertThrows(InsufficientFundsException.class, () -> account.withdraw(6_001));
        assertEquals(1_000, account.getBalance(), DELTA);
    }

    @Test
    void failedWithdrawalDoesNotChangeBalance() {
        CreditAccount account = new CreditAccount(number(3), "Ivan", 1_000, 5_000);

        assertThrows(InsufficientFundsException.class, () -> account.withdraw(10_000));
        assertEquals(1_000, account.getBalance(), DELTA);
    }
}
