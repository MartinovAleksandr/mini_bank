package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransferServiceTest {

    private static final double DELTA = 0.0001;

    private static TransferService service(CommissionPolicy policy) {
        return new TransferService(policy, message -> {});
    }

    @Test
    void successfulTransferChangesBothBalances() {
        DebitAccount from = new DebitAccount("001", "Alice", 10_000);
        DebitAccount to = new DebitAccount("002", "Bob", 1_000);

        boolean result = service(new NoCommission()).transfer(from, to, 3_000);

        assertTrue(result);
        assertEquals(7_000, from.getBalance(), DELTA);
        assertEquals(4_000, to.getBalance(), DELTA);
    }

    @Test
    void failedTransferDoesNotChangeBalances() {
        DebitAccount from = new DebitAccount("001", "Alice", 1_000);
        DebitAccount to = new DebitAccount("002", "Bob", 2_000);

        boolean result = service(new NoCommission()).transfer(from, to, 5_000);

        assertFalse(result);
        assertEquals(1_000, from.getBalance(), DELTA);
        assertEquals(2_000, to.getBalance(), DELTA);
    }

    @Test
    void negativeTransferIsRejected() {
        DebitAccount from = new DebitAccount("001", "Alice", 10_000);
        DebitAccount to = new DebitAccount("002", "Bob", 0);

        boolean result = service(new NoCommission()).transfer(from, to, -100);

        assertFalse(result);
        assertEquals(10_000, from.getBalance(), DELTA);
        assertEquals(0, to.getBalance(), DELTA);
    }

    @Test
    void zeroTransferIsRejected() {
        DebitAccount from = new DebitAccount("001", "Alice", 10_000);
        DebitAccount to = new DebitAccount("002", "Bob", 0);

        boolean result = service(new NoCommission()).transfer(from, to, 0);

        assertFalse(result);
        assertEquals(10_000, from.getBalance(), DELTA);
        assertEquals(0, to.getBalance(), DELTA);
    }

    @Test
    void transferToSameAccountIsRejected() {
        DebitAccount account = new DebitAccount("001", "Alice", 10_000);

        boolean result = service(new NoCommission()).transfer(account, account, 1_000);

        assertFalse(result);
        assertEquals(10_000, account.getBalance(), DELTA);
    }

    @Test
    void commissionIsChargedToSender() {
        DebitAccount from = new DebitAccount("001", "Alice", 20_000);
        DebitAccount to = new DebitAccount("002", "Bob", 0);

        boolean result = service(new PercentCommission(1)).transfer(from, to, 10_000);

        assertTrue(result);
        assertEquals(9_900, from.getBalance(), DELTA);
    }

    @Test
    void recipientReceivesExactTransferAmount() {
        DebitAccount from = new DebitAccount("001", "Alice", 20_000);
        DebitAccount to = new DebitAccount("002", "Bob", 0);

        service(new PercentCommission(1)).transfer(from, to, 10_000);

        assertEquals(10_000, to.getBalance(), DELTA);
    }

    @Test
    void transferIsRejectedWhenCommissionCannotBePaid() {
        DebitAccount from = new DebitAccount("001", "Alice", 10_050);
        DebitAccount to = new DebitAccount("002", "Bob", 0);

        boolean result = service(new PercentCommission(1)).transfer(from, to, 10_000);

        assertFalse(result);
        assertEquals(10_050, from.getBalance(), DELTA);
        assertEquals(0, to.getBalance(), DELTA);
    }

    @Test
    void debitToDebitTransfer() {
        DebitAccount from = new DebitAccount("001", "Alice", 5_000);
        DebitAccount to = new DebitAccount("002", "Bob", 1_000);

        boolean result = service(new NoCommission()).transfer(from, to, 2_000);

        assertTrue(result);
        assertEquals(3_000, from.getBalance(), DELTA);
        assertEquals(3_000, to.getBalance(), DELTA);
    }

    @Test
    void debitToSavingsTransfer() {
        DebitAccount from = new DebitAccount("001", "Alice", 5_000);
        SavingsAccount to = new SavingsAccount("002", "Bob", 1_000, 500);

        boolean result = service(new NoCommission()).transfer(from, to, 2_000);

        assertTrue(result);
        assertEquals(3_000, from.getBalance(), DELTA);
        assertEquals(3_000, to.getBalance(), DELTA);
    }

    @Test
    void creditToDebitTransfer() {
        CreditAccount from = new CreditAccount("001", "Alice", 1_000, 5_000);
        DebitAccount to = new DebitAccount("002", "Bob", 0);

        boolean result = service(new NoCommission()).transfer(from, to, 2_000);

        assertTrue(result);
        assertEquals(-1_000, from.getBalance(), DELTA);
        assertEquals(2_000, to.getBalance(), DELTA);
    }

    @Test
    void savingsToDebitTransfer() {
        SavingsAccount from = new SavingsAccount("001", "Alice", 5_000, 1_000);
        DebitAccount to = new DebitAccount("002", "Bob", 0);

        boolean result = service(new NoCommission()).transfer(from, to, 2_000);

        assertTrue(result);
        assertEquals(3_000, from.getBalance(), DELTA);
        assertEquals(2_000, to.getBalance(), DELTA);
    }
}
