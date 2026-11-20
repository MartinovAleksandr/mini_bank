package bank;

public class SavingsAccount extends BankAccount {
    private final double minimumBalance;

    public SavingsAccount(String number, String owner,
                          double initialBalance, double minimumBalance) {
        super(number, owner, initialBalance);
        if (minimumBalance < 0) {
            throw new IllegalArgumentException("Минимальный остаток не может быть отрицательным");
        }
        this.minimumBalance = minimumBalance;
    }

    @Override
    protected boolean canWithdraw(double amount) {
        return amount > 0 && getBalance() - amount >= minimumBalance;
    }

    @Override
    public boolean withdraw(double amount) {
        if (!canWithdraw(amount)) return false;
        decreaseBalance(amount);
        return true;
    }
}
