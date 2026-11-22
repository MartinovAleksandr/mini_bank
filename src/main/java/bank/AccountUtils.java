package bank;

import java.util.List;

public final class AccountUtils {
    private AccountUtils() {
    }

    public static double totalBalance(List<? extends BankAccount> accounts) {
        double total = 0;
        for (BankAccount account : accounts) {
            total += account.getBalance();
        }
        return total;
    }

    public static void addDemoDebitAccounts(List<? super DebitAccount> target) {
        target.add(new DebitAccount(new AccountNumber("0000000001"), "Demo1", 1_000));
        target.add(new DebitAccount(new AccountNumber("0000000002"), "Demo2", 2_000));
    }

    public static <T> void copy(List<? extends T> source, List<? super T> target) {
        for (T value : source) {
            target.add(value);
        }
    }
}
