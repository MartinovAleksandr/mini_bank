package bank;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

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

    @Test
    void sortedAccountsReturnsSortedCopyWithoutMutatingRepository() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BankService bankService = new BankService(repository, new TransferService(new NoCommission(), message -> {}));
        BankAccount a = new DebitAccount(number(3), "C", 300);
        BankAccount b = new CreditAccount(number(1), "A", 100, 0);
        BankAccount c = new DebitAccount(number(2), "B", 200);
        bankService.openAccount(a);
        bankService.openAccount(b);
        bankService.openAccount(c);

        List<BankAccount> original = repository.findAll();
        List<BankAccount> byBalance = bankService.sortedAccounts(Comparator.comparingDouble(BankAccount::getBalance));
        List<BankAccount> byBalanceDesc = bankService.sortedAccounts(Comparator.comparingDouble(BankAccount::getBalance).reversed());
        List<BankAccount> byNumber = bankService.sortedAccounts(Comparator.comparing(acc -> acc.getId().value()));
        List<BankAccount> byTypeThenBalance = bankService.sortedAccounts(
                Comparator.comparing((BankAccount acc) -> acc.getClass().getSimpleName())
                        .thenComparingDouble(BankAccount::getBalance));

        assertEquals(List.of(b, c, a), byBalance);
        assertEquals(List.of(a, c, b), byBalanceDesc);
        assertEquals(List.of(b, c, a), byNumber);
        assertEquals(List.of(b, c, a), byTypeThenBalance);
        assertEquals(original, repository.findAll());
    }

    @Test
    void getAccountSummariesMapsAccounts() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BankService bankService = new BankService(repository, new TransferService(new NoCommission(), message -> {}));
        bankService.openAccount(new DebitAccount(number(1), "Alice", 100));
        bankService.openAccount(new CreditAccount(number(2), "Bob", 200, 0));

        List<AccountSummary> summaries = bankService.getAccountSummaries();

        assertEquals(2, summaries.size());
        assertEquals("DebitAccount", summaries.get(0).type());
        assertEquals(100.0, summaries.get(0).balance(), 0.0001);
        assertEquals("CreditAccount", summaries.get(1).type());
    }

    @Test
    void streamFilterMethodsReturnMatchingAccounts() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BankService bankService = new BankService(repository, new TransferService(new NoCommission(), message -> {}));
        DebitAccount d1 = new DebitAccount(number(1), "Alice", 100_000);
        CreditAccount c1 = new CreditAccount(number(2), "Bob", 50_000, 0);
        DebitAccount d2 = new DebitAccount(number(3), "Alice", 10_000);
        bankService.openAccount(d1);
        bankService.openAccount(c1);
        bankService.openAccount(d2);

        assertEquals(List.of(d1), bankService.findRichAccounts(100_000));
        assertEquals(List.of(d1, d2), bankService.findDebitAccounts());
        assertEquals(List.of(d1, d2), bankService.findAccountsByOwner("Alice"));
        assertTrue(new BankService(new InMemoryRepository<>(), new TransferService(new NoCommission(), message -> {}))
                .findRichAccounts(1).isEmpty());
    }

    @Test
    void findAccountsSupportsPredicateComposition() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        BlockedAccountRegistry blocked = new BlockedAccountRegistry();
        BankService bankService = new BankService(repository, new TransferService(new NoCommission(), message -> {}, blocked));
        DebitAccount richDebit = new DebitAccount(number(1), "A", 150_000);
        DebitAccount blockedRichDebit = new DebitAccount(number(2), "B", 200_000);
        CreditAccount richCredit = new CreditAccount(number(3), "C", 120_000, 0);
        DebitAccount poorDebit = new DebitAccount(number(4), "D", 10_000);
        bankService.openAccount(richDebit);
        bankService.openAccount(blockedRichDebit);
        bankService.openAccount(richCredit);
        bankService.openAccount(poorDebit);
        blocked.block(blockedRichDebit.getNumber());

        Predicate<BankAccount> rich = a -> a.getBalance() >= 100_000;
        Predicate<BankAccount> notBlocked = a -> !blocked.isBlocked(a.getId());
        Predicate<BankAccount> debit = DebitAccount.class::isInstance;

        assertEquals(3, bankService.findAccounts(rich).size());
        assertEquals(3, bankService.findAccounts(notBlocked).size());
        assertEquals(3, bankService.findAccounts(debit).size());
        assertEquals(List.of(richDebit), bankService.findAccounts(rich.and(notBlocked).and(debit)));
        assertEquals(List.of(richCredit), bankService.findAccounts(debit.negate()));
        assertEquals(3, bankService.findAccounts(rich.or(CreditAccount.class::isInstance)).size());
    }
}
