package org.example;

import bank.BankAccount;
import bank.DebitAccount;
import bank.SavingsAccount;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== МИНИ-БАНК ===");
        System.out.print("Введите номер счёта: ");
        String number = scanner.nextLine();

        System.out.print("Введите владельца: ");
        String owner = scanner.nextLine();

        System.out.print("Начальный баланс: ");
        double balance = readDouble(scanner);

        System.out.println("Выберите тип счёта:");
        System.out.println("1 - Дебетовый (DebitAccount)");
        System.out.println("2 - Сберегательный (SavingsAccount)");
        int type = readInt(scanner);

        BankAccount account;
        if (type == 2) {
            System.out.print("Введите минимальный остаток: ");
            double minBalance = readDouble(scanner);
            account = new SavingsAccount(number, owner, balance, minBalance);
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
                return Double.parseDouble(scanner.nextLine().trim());
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
