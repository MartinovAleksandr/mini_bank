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
        // Снять можно, если сумма валидна и итоговый баланс не упадет ниже -creditLimit
        return isValidAmount(amount) && (getBalance() - amount >= -creditLimit);
    }

    @Override
    public boolean withdraw(double amount) {
        if (!canWithdraw(amount)) {
            return false;
        }
        decreaseBalance(amount);
        return true;
    }

    public double getCreditLimit() {
        return creditLimit;
    }
}

