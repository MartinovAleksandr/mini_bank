package bank;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FunctionalInterfaceUsageTest {

    private static AccountNumber number(int value) {
        return new AccountNumber(String.format("%010d", value));
    }

    @Test
    void transferWithLambdaCommissionPolicies() {
        DebitAccount from = new DebitAccount(number(1), "Alice", 10_000);
        DebitAccount to = new DebitAccount(number(2), "Bob", 1_000);
        CommissionPolicy noCommission = amount -> 0.0;
        TransferService service = new TransferService(noCommission, message -> {});

        service.transfer(from, to, 1_000);

        assertEquals(9_000, from.getBalance(), 0.0001);
        assertEquals(2_000, to.getBalance(), 0.0001);

        DebitAccount from2 = new DebitAccount(number(3), "Alice", 10_000);
        DebitAccount to2 = new DebitAccount(number(4), "Bob", 1_000);
        CommissionPolicy onePercent = amount -> amount * 0.01;
        TransferService service2 = new TransferService(onePercent, message -> {});

        service2.transfer(from2, to2, 1_000);

        assertEquals(8_990, from2.getBalance(), 0.0001);
        assertEquals(2_000, to2.getBalance(), 0.0001);
    }

    @Test
    void notificationWithLambdaAndMethodReference() {
        List<String> messages = new ArrayList<>();
        NotificationService lambda = message -> messages.add(message);
        lambda.notify("hello");

        NotificationService methodRef = messages::add;
        methodRef.notify("world");

        assertEquals(List.of("hello", "world"), messages);
    }
}
