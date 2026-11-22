package org.example;

import bank.AccountNumber;
import bank.Transaction;
import bank.TransactionStatus;
import bank.TransactionType;

public class TransactionDemo {
    public static void main(String[] args) {
        Transaction deposit = new Transaction(
                TransactionType.DEPOSIT,
                new AccountNumber("1234567890"),
                5_000,
                TransactionStatus.SUCCESS);

        Transaction withdrawal = new Transaction(
                TransactionType.WITHDRAWAL,
                new AccountNumber("1234567890"),
                1_500,
                TransactionStatus.REJECTED);

        Transaction transfer = new Transaction(
                TransactionType.TRANSFER,
                new AccountNumber("0987654321"),
                3_000,
                TransactionStatus.SUCCESS);

        System.out.println(deposit);
        System.out.println(withdrawal);
        System.out.println(transfer);
    }
}
