package bank;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class TransactionIndexTest {

    private static AccountNumber number(int value) {
        return new AccountNumber(String.format("%010d", value));
    }

    @Test
    void addMakesTransactionFindableByEqualAccountNumber() {
        TransactionIndex index = new TransactionIndex();
        AccountNumber stored = new AccountNumber("1111111111");
        AccountNumber lookup = new AccountNumber("1111111111");
        Transaction tx = new Transaction(1L, TransactionType.TRANSFER, stored, 100, TransactionStatus.SUCCESS);

        index.add(tx);

        List<Transaction> found = index.findByAccount(lookup);
        assertEquals(1, found.size());
        assertSame(tx, found.get(0));
    }

    @Test
    void findByAccountReturnsCopy() {
        TransactionIndex index = new TransactionIndex();
        AccountNumber account = number(1);
        Transaction tx = new Transaction(1L, TransactionType.TRANSFER, account, 100, TransactionStatus.SUCCESS);
        index.add(tx);

        List<Transaction> copy = index.findByAccount(account);
        copy.clear();

        assertEquals(1, index.findByAccount(account).size());
    }

    @Test
    void addIsReflectedInJournalAndIndex() {
        TransactionJournal journal = new TransactionJournal();
        TransactionIndex index = new TransactionIndex();
        Transaction tx = new Transaction(1L, TransactionType.TRANSFER, number(1), 100, TransactionStatus.SUCCESS);

        journal.add(tx);
        index.add(tx);

        assertEquals(1, journal.findByAccount(number(1)).size());
        assertEquals(1, index.findByAccount(number(1)).size());
        assertSame(tx, journal.findByAccount(number(1)).get(0));
        assertSame(tx, index.findByAccount(number(1)).get(0));
    }
}
