package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DebitAccountTest {

    private static final double DELTA = 0.0001;

    private static AccountNumber number(int value) {
        return new AccountNumber(String.format("%010d", value));
    }

    @Test
    void initialBalanceIsPreserved() {
        DebitAccount account = new DebitAccount(number(1), "Ivan", 1_000);

        assertEquals(1_000, account.getBalance(), DELTA);
    }

    @Test
    void depositIncreasesBalance() {
        DebitAccount account = new DebitAccount(number(1), "Ivan", 1_000);

        account.deposit(500);

        assertEquals(1_500, account.getBalance(), DELTA);
    }

    @Test
    void zeroDepositDoesNotChangeBalance() {
        DebitAccount account = new DebitAccount(number(1), "Ivan", 1_000);

        account.deposit(0);

        assertEquals(1_000, account.getBalance(), DELTA);
    }

    @Test
    void negativeDepositDoesNotChangeBalance() {
        DebitAccount account = new DebitAccount(number(1), "Ivan", 1_000);

        account.deposit(-100);

        assertEquals(1_000, account.getBalance(), DELTA);
    }

    @Test
    void withdrawDecreasesBalance() {
        DebitAccount account = new DebitAccount(number(1), "Ivan", 1_000);

        boolean result = account.withdraw(300);

        assertTrue(result);
        assertEquals(700, account.getBalance(), DELTA);
    }

    @Test
    void cannotWithdrawMoreThanBalance() {
        DebitAccount account = new DebitAccount(number(1), "Ivan", 1_000);

        boolean result = account.withdraw(1_500);

        assertFalse(result);
        assertEquals(1_000, account.getBalance(), DELTA);
    }

    @Test
    void zeroWithdrawalIsRejected() {
        DebitAccount account = new DebitAccount(number(1), "Ivan", 1_000);

        boolean result = account.withdraw(0);

        assertFalse(result);
        assertEquals(1_000, account.getBalance(), DELTA);
    }

    @Test
    void negativeWithdrawalIsRejected() {
        DebitAccount account = new DebitAccount(number(1), "Ivan", 1_000);

        boolean result = account.withdraw(-100);

        assertFalse(result);
        assertEquals(1_000, account.getBalance(), DELTA);
    }
}
