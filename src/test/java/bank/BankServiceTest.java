package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BankServiceTest {

    private static AccountNumber number(int value) {
        return new AccountNumber(String.format("%010d", value));
    }

    @Test
    void openAccountAndFindAccount() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BankService bankService = new BankService(repository, new TransferService(new NoCommission(), message -> {}));
        DebitAccount account = new DebitAccount(number(1), "Alice", 1000);

        bankService.openAccount(account);

        assertSame(account, bankService.findAccount(number(1)));
    }

    @Test
    void openAccountWithSameIdReplacesByRepositoryContract() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BankService bankService = new BankService(repository, new TransferService(new NoCommission(), message -> {}));
        AccountNumber id = number(1);
        DebitAccount oldAccount = new DebitAccount(id, "Alice", 1000);
        DebitAccount newAccount = new DebitAccount(id, "Bob", 5000);

        bankService.openAccount(oldAccount);
        bankService.openAccount(newAccount);

        assertEquals(1, repository.size());
        assertSame(newAccount, bankService.findAccount(id));
    }

    @Test
    void closeAccountRemovesAccount() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BankService bankService = new BankService(repository, new TransferService(new NoCommission(), message -> {}));
        AccountNumber id = number(1);
        bankService.openAccount(new DebitAccount(id, "Alice", 1000));

        assertTrue(bankService.closeAccount(id));
        assertFalse(repository.existsById(id));
        assertThrows(EntityNotFoundException.class, () -> bankService.findAccount(id));
    }

    @Test
    void transferByAccountNumbers() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BankService bankService = new BankService(repository, new TransferService(new NoCommission(), message -> {}));
        DebitAccount from = new DebitAccount(number(1), "Alice", 10_000);
        DebitAccount to = new DebitAccount(number(2), "Bob", 1_000);
        bankService.openAccount(from);
        bankService.openAccount(to);

        bankService.transfer(number(1), number(2), 3_000);

        assertEquals(7_000, from.getBalance(), 0.0001);
        assertEquals(4_000, to.getBalance(), 0.0001);
    }

    @Test
    void transferMissingSenderThrowsAndKeepsBalances() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BankService bankService = new BankService(repository, new TransferService(new NoCommission(), message -> {}));
        DebitAccount to = new DebitAccount(number(2), "Bob", 1_000);
        bankService.openAccount(to);

        assertThrows(EntityNotFoundException.class, () -> bankService.transfer(number(1), number(2), 100));
        assertEquals(1_000, to.getBalance(), 0.0001);
    }

    @Test
    void transferMissingReceiverThrowsAndKeepsBalances() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BankService bankService = new BankService(repository, new TransferService(new NoCommission(), message -> {}));
        DebitAccount from = new DebitAccount(number(1), "Alice", 10_000);
        bankService.openAccount(from);

        assertThrows(EntityNotFoundException.class, () -> bankService.transfer(number(1), number(2), 100));
        assertEquals(10_000, from.getBalance(), 0.0001);
    }

    @Test
    void transferFromBlockedAccountThrowsAndKeepsBalances() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BlockedAccountRegistry registry = new BlockedAccountRegistry();
        registry.block(number(1));
        BankService bankService = new BankService(repository, new TransferService(new NoCommission(), message -> {}, registry));
        DebitAccount from = new DebitAccount(number(1), "Alice", 10_000);
        DebitAccount to = new DebitAccount(number(2), "Bob", 1_000);
        bankService.openAccount(from);
        bankService.openAccount(to);

        assertThrows(IllegalStateException.class, () -> bankService.transfer(number(1), number(2), 3_000));
        assertEquals(10_000, from.getBalance(), 0.0001);
        assertEquals(1_000, to.getBalance(), 0.0001);
    }

    @Test
    void transferInsufficientFundsThrowsAndKeepsBalances() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BankService bankService = new BankService(repository, new TransferService(new NoCommission(), message -> {}));
        DebitAccount from = new DebitAccount(number(1), "Alice", 1_000);
        DebitAccount to = new DebitAccount(number(2), "Bob", 1_000);
        bankService.openAccount(from);
        bankService.openAccount(to);

        assertThrows(InsufficientFundsException.class, () -> bankService.transfer(number(1), number(2), 3_000));
        assertEquals(1_000, from.getBalance(), 0.0001);
        assertEquals(1_000, to.getBalance(), 0.0001);
    }
}
