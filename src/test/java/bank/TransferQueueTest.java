package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransferQueueTest {

    private static AccountNumber number(int value) {
        return new AccountNumber(String.format("%010d", value));
    }

    @Test
    void nextReturnsRequestsInFifoOrder() {
        TransferQueue queue = new TransferQueue();
        TransferRequest a = new TransferRequest(number(1), number(2), 100);
        TransferRequest b = new TransferRequest(number(2), number(3), 200);
        TransferRequest c = new TransferRequest(number(3), number(4), 300);

        queue.add(a);
        queue.add(b);
        queue.add(c);

        assertSame(a, queue.next());
        assertSame(b, queue.next());
        assertSame(c, queue.next());
    }

    @Test
    void peekDoesNotRemoveElement() {
        TransferQueue queue = new TransferQueue();
        TransferRequest request = new TransferRequest(number(1), number(2), 100);
        queue.add(request);

        assertSame(request, queue.peek());
        assertEquals(1, queue.size());
        assertFalse(queue.isEmpty());
    }

    @Test
    void nextOnEmptyQueueReturnsNull() {
        TransferQueue queue = new TransferQueue();

        assertNull(queue.next());
        assertNull(queue.peek());
        assertTrue(queue.isEmpty());
    }

    @Test
    void sizeDecreasesAfterNext() {
        TransferQueue queue = new TransferQueue();
        queue.add(new TransferRequest(number(1), number(2), 100));
        queue.add(new TransferRequest(number(2), number(3), 200));

        queue.next();

        assertEquals(1, queue.size());
    }
}
