package bank;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
