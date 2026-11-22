package bank;

public class SavingsAccount extends BankAccount {
    private final double minimumBalance;

    public SavingsAccount(AccountNumber number, String owner,
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
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (!canWithdraw(amount)) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        decreaseBalance(amount);
    }
}
