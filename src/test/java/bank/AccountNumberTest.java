package bank;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountNumberTest {

    @Test
    void validNumberIsCreated() {
        AccountNumber number = new AccountNumber("1234567890");

        assertEquals("1234567890", number.value());
    }

    @Test
    void numberWithLeadingZerosIsValid() {
        AccountNumber number = new AccountNumber("0000000001");

        assertEquals("0000000001", number.value());
    }

    @Test
    void nullIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber(null));
    }

    @Test
    void emptyStringIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber(""));
    }

    @Test
    void nineDigitsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("123456789"));
    }

    @Test
    void elevenDigitsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("12345678901"));
    }

    @Test
    void lettersInsideNumberAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("12345abc90"));
    }
}
