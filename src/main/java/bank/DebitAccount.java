package bank;

public class DebitAccount extends BankAccount {
    public DebitAccount(AccountNumber number, String owner, double initialBalance) {
        super(number, owner, initialBalance);
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
