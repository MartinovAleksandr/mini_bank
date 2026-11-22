package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BankAccountTest {

    private static AccountNumber number(int value) {
        return new AccountNumber(String.format("%010d", value));
    }

    @Test
    void accountsWithSameNumberAreEqual() {
        BankAccount debit = new DebitAccount(number(1), "Ivan", 1_000);
        BankAccount savings = new SavingsAccount(number(1), "Petr", 5_000, 500);

        assertEquals(debit, savings);
    }

    @Test
    void accountsWithDifferentNumbersAreNotEqual() {
        BankAccount first = new DebitAccount(number(1), "Ivan", 1_000);
        BankAccount second = new DebitAccount(number(2), "Ivan", 1_000);

        assertNotEquals(first, second);
    }

    @Test
    void accountEqualsItself() {
        BankAccount account = new DebitAccount(number(1), "Ivan", 1_000);

        assertEquals(account, account);
    }

    @Test
    void accountDoesNotEqualNull() {
        BankAccount account = new DebitAccount(number(1), "Ivan", 1_000);

        assertFalse(account.equals(null));
    }

    @Test
    void equalAccountsHaveSameHashCode() {
        BankAccount debit = new DebitAccount(number(1), "Ivan", 1_000);
        BankAccount credit = new CreditAccount(number(1), "Petr", 2_000, 5_000);

        assertEquals(debit.hashCode(), credit.hashCode());
    }

    @Test
    void toStringReflectsActualSubclass() {
        BankAccount account = new DebitAccount(number(1), "Ivan", 10_000);

        assertEquals("DebitAccount{number='0000000001', owner='Ivan', balance=10000.0}", account.toString());
    }
}
