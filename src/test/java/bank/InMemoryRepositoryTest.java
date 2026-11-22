package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryRepositoryTest {

    private static AccountNumber number(int value) {
        return new AccountNumber(String.format("%010d", value));
    }

    @Test
    void saveAndFindById() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        DebitAccount account = new DebitAccount(number(1), "Ivan", 1_000);

        repository.save(account);

        assertSame(account, repository.findById(number(1)));
        assertTrue(repository.existsById(number(1)));
        assertEquals(1, repository.size());
    }

    @Test
    void existsByIdReturnsFalseForMissingId() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();

        assertFalse(repository.existsById(number(1)));
    }

    @Test
    void sizeIncreasesAfterSavingNewId() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();

        repository.save(new DebitAccount(number(1), "Ivan", 1_000));
        repository.save(new DebitAccount(number(2), "Petr", 2_000));

        assertEquals(2, repository.size());
    }

    @Test
    void saveNullIsRejected() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();

        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
    }

    @Test
    void saveWithSameIdReplacesOldValue() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        DebitAccount original = new DebitAccount(number(1), "Ivan", 1_000);
        DebitAccount replacement = new DebitAccount(number(1), "Petr", 2_000);

        repository.save(original);
        repository.save(replacement);

        assertSame(replacement, repository.findById(number(1)));
        assertEquals(1, repository.size());
    }

    @Test
    void findByIdThrowsWhenNotFound() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();

        assertThrows(EntityNotFoundException.class, () -> repository.findById(number(1)));
    }

    @Test
    void deleteByIdRemovesExistingObject() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        repository.save(new DebitAccount(number(1), "Ivan", 1_000));

        boolean deleted = repository.deleteById(number(1));

        assertTrue(deleted);
        assertFalse(repository.existsById(number(1)));
        assertEquals(0, repository.size());
    }

    @Test
    void findAllReturnsAllSavedValues() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        DebitAccount first = new DebitAccount(number(1), "Ivan", 1_000);
        DebitAccount second = new DebitAccount(number(2), "Petr", 2_000);
        repository.save(first);
        repository.save(second);

        var all = repository.findAll();

        assertEquals(2, all.size());
        assertTrue(all.contains(first));
        assertTrue(all.contains(second));
    }

    @Test
    void supportsDifferentEntityTypes() {
        Repository<Long, Transaction> repository = new InMemoryRepository<>();
        Transaction transaction = new Transaction(
                1L, TransactionType.DEPOSIT, number(1), 500, TransactionStatus.SUCCESS);

        repository.save(transaction);

        assertEquals(transaction, repository.findById(1L));
        assertTrue(repository.existsById(1L));
        assertFalse(repository.existsById(2L));
    }
}
