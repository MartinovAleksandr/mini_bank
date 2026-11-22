package bank;

import java.util.Objects;

public final class GenericUtils {
    private GenericUtils() {
    }

    public static <T> T first(T[] values) {
        if (values == null || values.length == 0) {
            return null;
        }
        return values[0];
    }

    public static <T> T last(T[] values) {
        if (values == null || values.length == 0) {
            return null;
        }
        return values[values.length - 1];
    }

    public static <T> boolean contains(T[] values, T target) {
        if (values == null) {
            return false;
        }
        for (T value : values) {
            if (Objects.equals(value, target)) {
                return true;
            }
        }
        return false;
    }
}
