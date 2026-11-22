package bank;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionFunctionsTest {

    private static AccountNumber number(int value) {
        return new AccountNumber(String.format("%010d", value));
    }

    @Test
    void filterByPositiveBalance() {
        List<BankAccount> accounts = List.of(
                new DebitAccount(number(1), "A", 100),
                new DebitAccount(number(2), "B", 0),
                new DebitAccount(number(3), "C", 50)
        );
        Predicate<BankAccount> positiveBalance = account -> account.getBalance() > 0;

        List<BankAccount> result = CollectionFunctions.filter(accounts, positiveBalance);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(a -> a.getBalance() > 0));
    }

    @Test
    void filterByRichAndDebitOnly() {
        List<BankAccount> accounts = List.of(
                new DebitAccount(number(1), "A", 100_000),
                new CreditAccount(number(2), "B", 200_000, 0),
                new DebitAccount(number(3), "C", 50_000)
        );
        Predicate<BankAccount> rich = account -> account.getBalance() >= 100_000;
        Predicate<BankAccount> debitOnly = DebitAccount.class::isInstance;

        assertEquals(2, CollectionFunctions.filter(accounts, rich).size());
        assertEquals(2, CollectionFunctions.filter(accounts, debitOnly).size());
    }
}
