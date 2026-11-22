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
}
