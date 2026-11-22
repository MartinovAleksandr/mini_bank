package bank;

import java.util.Objects;

public abstract class BankAccount {
    private final AccountNumber number;
    private final String owner;
    private double balance;

    protected BankAccount(AccountNumber number, String owner, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Начальный баланс не может быть отрицательным");
        }
        this.number = number;
        this.owner = owner;
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        // Теперь используем protected-метод
        if (!isValidAmount(amount)) return;
        balance += amount;
    }

    public abstract boolean withdraw(double amount);

    public double getBalance() { return balance; }
    public AccountNumber getNumber() { return number; }
    public String getOwner() { return owner; }

    // --- Методы, доступные только наследникам (protected) ---

    /** Проверяет, что сумма больше нуля */
    protected boolean isValidAmount(double amount) {
        return amount > 0;
    }

    /** Проверяет, хватает ли средств для снятия (для обычного счёта) */
    protected boolean canWithdraw(double amount) {
        return isValidAmount(amount) && amount <= balance;
    }

    /** Изменяет баланс (доступно только наследникам) */
    protected void decreaseBalance(double amount) {
        balance -= amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BankAccount other)) {
            return false;
        }
        return Objects.equals(number, other.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "{number='" + number
                + "', owner='" + owner
                + "', balance=" + balance
                + "}";
    }
}
