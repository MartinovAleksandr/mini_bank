package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransferServiceTest {

    private static final double DELTA = 0.0001;

    private static AccountNumber number(int value) {
        return new AccountNumber(String.format("%010d", value));
    }

    private static TransferService service(CommissionPolicy policy) {
        return new TransferService(policy, message -> {});
    }

    @Test
    void successfulTransferChangesBothBalances() {
        DebitAccount from = new DebitAccount(number(1), "Alice", 10_000);
        DebitAccount to = new DebitAccount(number(2), "Bob", 1_000);

        service(new NoCommission()).transfer(from, to, 3_000);
        assertEquals(7_000, from.getBalance(), DELTA);
        assertEquals(4_000, to.getBalance(), DELTA);
    }

    @Test
    void failedTransferDoesNotChangeBalances() {
        DebitAccount from = new DebitAccount(number(1), "Alice", 1_000);
        DebitAccount to = new DebitAccount(number(2), "Bob", 2_000);

        assertThrows(InsufficientFundsException.class,
                () -> service(new NoCommission()).transfer(from, to, 5_000));
        assertEquals(1_000, from.getBalance(), DELTA);
        assertEquals(2_000, to.getBalance(), DELTA);
    }

    @Test
    void negativeTransferIsRejected() {
        DebitAccount from = new DebitAccount(number(1), "Alice", 10_000);
        DebitAccount to = new DebitAccount(number(2), "Bob", 0);

        assertThrows(IllegalArgumentException.class,
                () -> service(new NoCommission()).transfer(from, to, -100));
        assertEquals(10_000, from.getBalance(), DELTA);
        assertEquals(0, to.getBalance(), DELTA);
    }

    @Test
    void zeroTransferIsRejected() {
        DebitAccount from = new DebitAccount(number(1), "Alice", 10_000);
        DebitAccount to = new DebitAccount(number(2), "Bob", 0);

        assertThrows(IllegalArgumentException.class,
                () -> service(new NoCommission()).transfer(from, to, 0));
        assertEquals(10_000, from.getBalance(), DELTA);
        assertEquals(0, to.getBalance(), DELTA);
    }

    @Test
    void transferToSameAccountIsRejected() {
        DebitAccount account = new DebitAccount(number(1), "Alice", 10_000);

        assertThrows(IllegalArgumentException.class,
                () -> service(new NoCommission()).transfer(account, account, 1_000));
        assertEquals(10_000, account.getBalance(), DELTA);
    }

    @Test
    void commissionIsChargedToSender() {
        DebitAccount from = new DebitAccount(number(1), "Alice", 20_000);
        DebitAccount to = new DebitAccount(number(2), "Bob", 0);

        service(new PercentCommission(1)).transfer(from, to, 10_000);
        assertEquals(9_900, from.getBalance(), DELTA);
    }

    @Test
    void recipientReceivesExactTransferAmount() {
        DebitAccount from = new DebitAccount(number(1), "Alice", 20_000);
        DebitAccount to = new DebitAccount(number(2), "Bob", 0);

        service(new PercentCommission(1)).transfer(from, to, 10_000);

        assertEquals(10_000, to.getBalance(), DELTA);
    }

    @Test
    void transferIsRejectedWhenCommissionCannotBePaid() {
        DebitAccount from = new DebitAccount(number(1), "Alice", 10_050);
        DebitAccount to = new DebitAccount(number(2), "Bob", 0);

        assertThrows(InsufficientFundsException.class,
                () -> service(new PercentCommission(1)).transfer(from, to, 10_000));
        assertEquals(10_050, from.getBalance(), DELTA);
        assertEquals(0, to.getBalance(), DELTA);
    }

    @Test
    void debitToDebitTransfer() {
        DebitAccount from = new DebitAccount(number(1), "Alice", 5_000);
        DebitAccount to = new DebitAccount(number(2), "Bob", 1_000);

        service(new NoCommission()).transfer(from, to, 2_000);
        assertEquals(3_000, from.getBalance(), DELTA);
        assertEquals(3_000, to.getBalance(), DELTA);
    }

    @Test
    void debitToSavingsTransfer() {
        DebitAccount from = new DebitAccount(number(1), "Alice", 5_000);
        SavingsAccount to = new SavingsAccount(number(2), "Bob", 1_000, 500);

        service(new NoCommission()).transfer(from, to, 2_000);
        assertEquals(3_000, from.getBalance(), DELTA);
        assertEquals(3_000, to.getBalance(), DELTA);
    }

    @Test
    void creditToDebitTransfer() {
        CreditAccount from = new CreditAccount(number(1), "Alice", 1_000, 5_000);
        DebitAccount to = new DebitAccount(number(2), "Bob", 0);

        service(new NoCommission()).transfer(from, to, 2_000);
        assertEquals(-1_000, from.getBalance(), DELTA);
        assertEquals(2_000, to.getBalance(), DELTA);
    }

    @Test
    void savingsToDebitTransfer() {
        SavingsAccount from = new SavingsAccount(number(1), "Alice", 5_000, 1_000);
        DebitAccount to = new DebitAccount(number(2), "Bob", 0);

        service(new NoCommission()).transfer(from, to, 2_000);
        assertEquals(3_000, from.getBalance(), DELTA);
        assertEquals(2_000, to.getBalance(), DELTA);
    }

    @Test
    void successfulTransferSendsExactlyOneNotification() {
        DebitAccount from = new DebitAccount(number(1), "Alice", 10_000);
        DebitAccount to = new DebitAccount(number(2), "Bob", 0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService transferService = new TransferService(new NoCommission(), notificationService);

        transferService.transfer(from, to, 3_000);

        assertEquals(1, notificationService.getNotificationCount());
    }

    @Test
    void failedTransferSendsNoNotification() {
        DebitAccount from = new DebitAccount(number(1), "Alice", 1_000);
        DebitAccount to = new DebitAccount(number(2), "Bob", 0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService transferService = new TransferService(new NoCommission(), notificationService);

        assertThrows(InsufficientFundsException.class,
                () -> transferService.transfer(from, to, 5_000));
        assertEquals(0, notificationService.getNotificationCount());
    }

    @Test
    void notificationMessageMatchesExpected() {
        DebitAccount from = new DebitAccount(number(1), "Alice", 10_000);
        DebitAccount to = new DebitAccount(number(2), "Bob", 0);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService transferService = new TransferService(new NoCommission(), notificationService);

        transferService.transfer(from, to, 3_000);

        assertEquals("Transfer 3000.0 completed", notificationService.getLastMessage());
    }
}

