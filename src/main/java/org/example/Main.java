package org.example;

import bank.BankAccount;
import bank.CommissionPolicy;
import bank.CreditAccount;
import bank.DebitAccount;
import bank.NoCommission;
import bank.PercentCommission;
import bank.SavingsAccount;
import bank.TransferService;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== НАСТРОЙКА БАНКА ===");
        System.out.println("Выберите политику комиссии за перевод:");
        System.out.println("1 - Без комиссии");
        System.out.println("2 - Процентная комиссия");
        int commType = readInt(scanner);

        CommissionPolicy policy;
        if (commType == 2) {
            System.out.print("Введите процент комиссии (например, 1 для 1%): ");
            double percent = readDouble(scanner);
            policy = new PercentCommission(percent);
        } else {
            policy = new NoCommission();
        }
        TransferService transferService = new TransferService(policy);

        System.out.println("\n=== СОЗДАНИЕ СЧЁТА ===");
        System.out.print("Введите номер счёта: ");
        String number = scanner.nextLine();

        System.out.print("Введите владельца: ");
        String owner = scanner.nextLine();

        System.out.print("Начальный баланс: ");
        double balance = readDouble(scanner);

        System.out.println("Выберите тип счёта:");
        System.out.println("1 - Дебетовый (DebitAccount)");
        System.out.println("2 - Сберегательный (SavingsAccount)");
        System.out.println("3 - Кредитный (CreditAccount)");
        int type = readInt(scanner);

        BankAccount account;
        if (type == 2) {
            System.out.print("Введите минимальный остаток: ");
            double minBalance = readDouble(scanner);
            account = new SavingsAccount(number, owner, balance, minBalance);
        } else if (type == 3) {
            System.out.print("Введите кредитный лимит: ");
            double creditLimit = readDouble(scanner);
            account = new CreditAccount(number, owner, balance, creditLimit);
        } else {
            account = new DebitAccount(number, owner, balance);
        }

        System.out.println("\nСчёт создан: " + account.getOwner()
                + " | Баланс: " + account.getBalance());

        while (true) {
            System.out.println("\n--- МЕНЮ ---");
            System.out.println("1. Пополнить (deposit)");
            System.out.println("2. Снять (withdraw)");
            System.out.println("3. Показать баланс");
            System.out.println("4. Сделать перевод (transfer)");
            System.out.println("0. Выход");
            System.out.print("Выбор: ");

            int choice = readInt(scanner);

            if (choice == 1){
                System.out.print("Сумма для пополнения: ");
                double dep = readDouble(scanner);
                account.deposit(dep);
                System.out.println("Баланс после пополнения: " + account.getBalance());
            }
            else if (choice == 2){
                System.out.print("Сумма для снятия: ");
                double wd = readDouble(scanner);
                boolean ok = account.withdraw(wd);
                if (ok) {
                    System.out.println("Успешно. Новый баланс: " + account.getBalance());
                } else {
                    System.out.println("Отказано. Баланс не изменился: " + account.getBalance());
                }
            }
            else if (choice == 3){
                System.out.println("Текущий баланс: " + account.getBalance());
            }
            else if (choice == 4){
                System.out.println("--- Создание счёта получателя ---");
                System.out.print("Введите номер счёта получателя: ");
                String toNumber = scanner.nextLine();
                if (toNumber.equals(account.getNumber())) {
                    System.out.println("Ошибка: нельзя перевести на тот же самый счёт.");
                    continue;
                }

                System.out.print("Введите имя получателя: ");
                String toOwner = scanner.nextLine();
                BankAccount toAccount = new DebitAccount(toNumber, toOwner, 0);

                System.out.print("Сумма перевода: ");
                double amount = readDouble(scanner);

                double balanceBeforeTransfer = account.getBalance();
                boolean success = transferService.transfer(account, toAccount, amount);
                if (success) {
                    System.out.println("Перевод успешно выполнен!");
                    System.out.println("Списано со счёта (с учётом комиссии): " + (balanceBeforeTransfer - account.getBalance()));
                    System.out.println("Ваш новый баланс: " + account.getBalance());
                    System.out.println("Баланс получателя (" + toOwner + "): " + toAccount.getBalance());
                } else {
                    System.out.println("Перевод не удался (недостаточно средств или неверная сумма).");
                }
            }
            else if (choice == 0){
                System.out.println("Выход...");
                return;
            }
            else{
                System.out.println("Нет такого пункта.");
            }
        }
    }

    private static double readDouble(Scanner scanner) {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.print("Введите число: ");
            }
        }
    }

    private static int readInt(Scanner scanner) {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Введите целое число: ");
            }
        }
    }
}