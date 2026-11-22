package bank;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BankReports {

    public Map<AccountNumber, Integer> countByAccount(List<Transaction> transactions) {
        Map<AccountNumber, Integer> result = new HashMap<>();
        transactions.stream().forEach(tx -> result.put(tx.account(), result.getOrDefault(tx.account(), 0) + 1));
        return result;
    }

    public Map<AccountNumber, Double> totalAmountByAccount(List<Transaction> transactions) {
        Map<AccountNumber, Double> result = new HashMap<>();
        transactions.stream().forEach(tx -> result.put(tx.account(), result.getOrDefault(tx.account(), 0.0) + tx.amount()));
        return result;
    }
}
