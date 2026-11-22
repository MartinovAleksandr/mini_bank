package org.example;

import bank.AccountNumber;
import bank.BankAccount;
import bank.ConsoleNotificationService;
import bank.DebitAccount;
import bank.InsufficientFundsException;
import bank.NoCommission;
import bank.TransferService;

public class TransferDemo {
    public static void main(String[] args) {
        BankAccount from = new DebitAccount(new AccountNumber("1234567890"), "Ivan", 1_000);
        BankAccount to = new DebitAccount(new AccountNumber("0987654321"), "Petr", 0);
        TransferService transferService = new TransferService(
                new NoCommission(), new ConsoleNotificationService());

        try {
            transferService.transfer(from, to, 5_000);
            System.out.println("Transfer completed");
        } catch (InsufficientFundsException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        }
    }
}
