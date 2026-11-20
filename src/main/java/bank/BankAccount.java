package bank;

public abstract class BankAccount {
    private final String number;
    private final String owner;
    private double balance;

    protected BankAccount(String number, String owner, double initialBalance) {
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
    public String getNumber() { return number; }
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
}
