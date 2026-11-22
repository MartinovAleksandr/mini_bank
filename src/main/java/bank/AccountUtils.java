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

    public static <T extends BankAccount> T richest(List<T> accounts) {
        if (accounts == null || accounts.isEmpty()) {
            throw new IllegalArgumentException("Accounts must not be empty");
        }
        T richest = accounts.get(0);
        for (int i = 1; i < accounts.size(); i++) {
            T account = accounts.get(i);
            if (account.getBalance() > richest.getBalance()) {
                richest = account;
            }
        }
        return richest;
    }
}
