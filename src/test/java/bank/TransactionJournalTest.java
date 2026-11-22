package bank;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class TransactionJournalTest {

    private static AccountNumber number(int value) {
        return new AccountNumber(String.format("%010d", value));
    }

    @Test
    void transactionsAreStoredInInsertionOrder() {
        TransactionJournal journal = new TransactionJournal();
        Transaction first = new Transaction(1L, TransactionType.TRANSFER, number(1), 100, TransactionStatus.SUCCESS);
        Transaction second = new Transaction(2L, TransactionType.TRANSFER, number(2), 200, TransactionStatus.SUCCESS);

        journal.add(first);
        journal.add(second);

        List<Transaction> all = journal.findAll();
        assertSame(first, all.get(0));
        assertSame(second, all.get(1));
    }

    @Test
    void sameTransactionCanBeAddedTwice() {
        TransactionJournal journal = new TransactionJournal();
        Transaction tx = new Transaction(1L, TransactionType.TRANSFER, number(1), 100, TransactionStatus.SUCCESS);

        journal.add(tx);
        journal.add(tx);

        assertEquals(2, journal.size());
    }

    @Test
    void returnedListDoesNotExposeInternalState() {
        TransactionJournal journal = new TransactionJournal();
        Transaction tx = new Transaction(1L, TransactionType.TRANSFER, number(1), 100, TransactionStatus.SUCCESS);
        journal.add(tx);

        List<Transaction> copy = journal.findAll();
        copy.clear();

        assertEquals(1, journal.size());
    }
}
