package bank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamBasicsTest {

    private static AccountNumber number(int value) {
        return new AccountNumber(String.format("%010d", value));
    }

    @Test
    void loopAndStreamProduceSameResult() {
        List<BankAccount> accounts = List.of(
                new DebitAccount(number(1), "A", 50_000),
                new DebitAccount(number(2), "B", 150_000),
                new DebitAccount(number(3), "C", 100_000)
        );

        List<BankAccount> loopResult = new ArrayList<>();
        for (BankAccount account : accounts) {
            if (account.getBalance() >= 100_000) {
                loopResult.add(account);
            }
        }

        List<BankAccount> streamResult = accounts.stream()
                .filter(a -> a.getBalance() >= 100_000)
                .toList();

        assertEquals(loopResult, streamResult);
    }
}
