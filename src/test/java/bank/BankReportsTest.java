package bank;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankReportsTest {

    private static AccountNumber number(String value) {
        return new AccountNumber(value);
    }

    @Test
    void countByAccountEmpty() {
        BankReports reports = new BankReports();
        assertTrue(reports.countByAccount(List.of()).isEmpty());
    }

    @Test
    void countByAccountSingleAccount() {
        BankReports reports = new BankReports();
        List<Transaction> txs = List.of(
                new Transaction(1L, TransactionType.TRANSFER, number("0000000001"), 100, TransactionStatus.SUCCESS)
        );

        Map<AccountNumber, Integer> result = reports.countByAccount(txs);

        assertEquals(1, result.get(number("0000000001")));
    }

    @Test
    void countByAccountMultipleAccountsAndSameAccount() {
        BankReports reports = new BankReports();
        List<Transaction> txs = List.of(
                new Transaction(1L, TransactionType.TRANSFER, number("0000000001"), 100, TransactionStatus.SUCCESS),
                new Transaction(2L, TransactionType.TRANSFER, number("0000000002"), 200, TransactionStatus.SUCCESS),
                new Transaction(3L, TransactionType.DEPOSIT, number("0000000001"), 50, TransactionStatus.SUCCESS)
        );

        Map<AccountNumber, Integer> result = reports.countByAccount(txs);

        assertEquals(2, result.get(number("0000000001")));
        assertEquals(1, result.get(number("0000000002")));
    }

    @Test
    void equalAccountNumbersAggregateIntoOneEntry() {
        BankReports reports = new BankReports();
        List<Transaction> txs = List.of(
                new Transaction(1L, TransactionType.TRANSFER, new AccountNumber("1111111111"), 100, TransactionStatus.SUCCESS),
                new Transaction(2L, TransactionType.TRANSFER, new AccountNumber("1111111111"), 50, TransactionStatus.SUCCESS)
        );

        Map<AccountNumber, Integer> counts = reports.countByAccount(txs);
        Map<AccountNumber, Double> totals = reports.totalAmountByAccount(txs);

        assertEquals(1, counts.size());
        assertEquals(2, counts.get(number("1111111111")));
        assertEquals(1, totals.size());
        assertEquals(150.0, totals.get(number("1111111111")), 0.0001);
    }

    @Test
    void totalAmountByAccountAggregatesAmounts() {
        BankReports reports = new BankReports();
        List<Transaction> txs = List.of(
                new Transaction(1L, TransactionType.TRANSFER, number("0000000001"), 100, TransactionStatus.SUCCESS),
                new Transaction(2L, TransactionType.TRANSFER, number("0000000002"), 200, TransactionStatus.SUCCESS),
                new Transaction(3L, TransactionType.WITHDRAWAL, number("0000000001"), 50, TransactionStatus.SUCCESS)
        );

        Map<AccountNumber, Double> result = reports.totalAmountByAccount(txs);

        assertEquals(150.0, result.get(number("0000000001")), 0.0001);
        assertEquals(200.0, result.get(number("0000000002")), 0.0001);
    }
}
