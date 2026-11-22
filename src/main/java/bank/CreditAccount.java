package bank;

public class CreditAccount extends BankAccount {
    private final double creditLimit;

    public CreditAccount(AccountNumber number, String owner, double initialBalance, double creditLimit) {
        super(number, owner, initialBalance);
        if (creditLimit < 0) {
            throw new IllegalArgumentException("Кредитный лимит не может быть отрицательным");
        }
        this.creditLimit = creditLimit;
    }

    @Override
    protected boolean canWithdraw(double amount) {
        return isValidAmount(amount) && (getBalance() - amount >= -creditLimit);
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

    public double getCreditLimit() {
        return creditLimit;
    }
}
