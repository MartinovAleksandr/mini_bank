package bank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComparatorDemoTest {

    private static AccountNumber number(int value) {
        return new AccountNumber(String.format("%010d", value));
    }

    @Test
    void anonymousLambdaAndFactoryComparatorsWork() {
        BankAccount a = new DebitAccount(number(1), "A", 300);
        BankAccount b = new DebitAccount(number(2), "B", 100);
        BankAccount c = new DebitAccount(number(3), "C", 200);

        Comparator<BankAccount> byBalanceAnonymous = new Comparator<>() {
            @Override
            public int compare(BankAccount x, BankAccount y) {
                return Double.compare(x.getBalance(), y.getBalance());
            }
        };
        Comparator<BankAccount> byBalanceLambda = (x, y) -> Double.compare(x.getBalance(), y.getBalance());
        Comparator<BankAccount> byBalanceFactory = Comparator.comparingDouble(BankAccount::getBalance);

        List<BankAccount> list1 = new ArrayList<>(List.of(a, b, c));
        List<BankAccount> list2 = new ArrayList<>(List.of(a, b, c));
        List<BankAccount> list3 = new ArrayList<>(List.of(a, b, c));

        list1.sort(byBalanceAnonymous);
        list2.sort(byBalanceLambda);
        list3.sort(byBalanceFactory);

        assertEquals(List.of(b, c, a), list1);
        assertEquals(List.of(b, c, a), list2);
        assertEquals(List.of(b, c, a), list3);
    }
}
