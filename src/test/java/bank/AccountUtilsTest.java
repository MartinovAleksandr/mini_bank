package bank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountUtilsTest {

    private static final double DELTA = 0.0001;

    private static AccountNumber number(int value) {
        return new AccountNumber(String.format("%010d", value));
    }

    @Test
    void totalBalanceAcceptsBankAccountList() {
        List<BankAccount> accounts = List.of(
                new DebitAccount(number(1), "Ivan", 1_000),
                new SavingsAccount(number(2), "Petr", 2_000, 100),
                new CreditAccount(number(3), "Anna", 500, 1_000));

        assertEquals(3_500, AccountUtils.totalBalance(accounts), DELTA);
    }

    @Test
    void totalBalanceAcceptsDebitAccountList() {
        List<DebitAccount> accounts = List.of(
                new DebitAccount(number(1), "Ivan", 1_000),
                new DebitAccount(number(2), "Petr", 2_500));

        assertEquals(3_500, AccountUtils.totalBalance(accounts), DELTA);
    }

    @Test
    void addDemoDebitAccountsAcceptsDebitAccountList() {
        List<DebitAccount> target = new ArrayList<>();

        AccountUtils.addDemoDebitAccounts(target);

        assertEquals(2, target.size());
        assertEquals(1_000, target.get(0).getBalance(), DELTA);
    }

    @Test
    void addDemoDebitAccountsAcceptsBankAccountList() {
        List<BankAccount> target = new ArrayList<>();

        AccountUtils.addDemoDebitAccounts(target);

        assertEquals(2, target.size());
    }

    @Test
    void addDemoDebitAccountsAcceptsObjectList() {
        List<Object> target = new ArrayList<>();

        AccountUtils.addDemoDebitAccounts(target);

        assertEquals(2, target.size());
    }

    @Test
    void copyDebitAccountsToBankAccountList() {
        List<DebitAccount> source = List.of(
                new DebitAccount(number(1), "Ivan", 1_000),
                new DebitAccount(number(2), "Petr", 2_000));
        List<BankAccount> target = new ArrayList<>();

        AccountUtils.copy(source, target);

        assertEquals(2, target.size());
        assertEquals(source.get(0), target.get(0));
        assertEquals(source.get(1), target.get(1));
    }

    @Test
    void copyDebitAccountsToObjectList() {
        List<DebitAccount> source = List.of(new DebitAccount(number(1), "Ivan", 1_000));
        List<Object> target = new ArrayList<>();

        AccountUtils.copy(source, target);

        assertEquals(1, target.size());
        assertEquals(source.get(0), target.get(0));
    }

    @Test
    void copyBankAccountsToBankAccountListPreservesOrder() {
        List<BankAccount> source = List.of(
                new DebitAccount(number(1), "Ivan", 1_000),
                new SavingsAccount(number(2), "Petr", 2_000, 100),
                new CreditAccount(number(3), "Anna", 500, 1_000));
        List<BankAccount> target = new ArrayList<>();

        AccountUtils.copy(source, target);

        assertEquals(source, target);
    }

    @Test
    void richestReturnsAccountWithMaxBalance() {
        DebitAccount first = new DebitAccount(number(1), "Ivan", 1_000);
        DebitAccount second = new DebitAccount(number(2), "Petr", 5_000);
        List<DebitAccount> accounts = List.of(first, second);

        DebitAccount richest = AccountUtils.richest(accounts);

        assertSame(second, richest);
    }

    @Test
    void richestRejectsEmptyList() {
        assertThrows(IllegalArgumentException.class, () -> AccountUtils.richest(List.of()));
    }

    @Test
    void richestReturnsSingleAccount() {
        DebitAccount account = new DebitAccount(number(1), "Ivan", 1_000);

        assertSame(account, AccountUtils.richest(List.of(account)));
    }

    @Test
    void richestReturnsFirstWhenBalancesAreEqual() {
        DebitAccount first = new DebitAccount(number(1), "Ivan", 2_000);
        DebitAccount second = new DebitAccount(number(2), "Petr", 2_000);

        DebitAccount richest = AccountUtils.richest(List.of(first, second));

        assertSame(first, richest);
    }
}
