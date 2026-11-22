package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BankAccountTest {

    @Test
    void accountsWithSameNumberAreEqual() {
        BankAccount debit = new DebitAccount("001", "Ivan", 1_000);
        BankAccount savings = new SavingsAccount("001", "Petr", 5_000, 500);

        assertEquals(debit, savings);
    }

    @Test
    void accountsWithDifferentNumbersAreNotEqual() {
        BankAccount first = new DebitAccount("001", "Ivan", 1_000);
        BankAccount second = new DebitAccount("002", "Ivan", 1_000);

        assertNotEquals(first, second);
    }

    @Test
    void accountEqualsItself() {
        BankAccount account = new DebitAccount("001", "Ivan", 1_000);

        assertEquals(account, account);
    }

    @Test
    void accountDoesNotEqualNull() {
        BankAccount account = new DebitAccount("001", "Ivan", 1_000);

        assertFalse(account.equals(null));
    }

    @Test
    void equalAccountsHaveSameHashCode() {
        BankAccount debit = new DebitAccount("001", "Ivan", 1_000);
        BankAccount credit = new CreditAccount("001", "Petr", 2_000, 5_000);

        assertEquals(debit.hashCode(), credit.hashCode());
    }

    @Test
    void toStringReflectsActualSubclass() {
        BankAccount account = new DebitAccount("001", "Ivan", 10_000);

        assertEquals("DebitAccount{number='001', owner='Ivan', balance=10000.0}", account.toString());
    }
}
