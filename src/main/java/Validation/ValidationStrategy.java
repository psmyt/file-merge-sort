package Validation;

import java.util.Comparator;
import java.util.function.Predicate;

public class ValidationStrategy {
    private final Order sortingOrder;

    private final Comparator<String> comparator;
    private final Predicate<String> validator;

    public ValidationStrategy(Comparator<String> comparator, Predicate<String> validator, Order sortingOrder) {
        this.comparator = comparator;
        this.validator = validator;
        this.sortingOrder = sortingOrder;
    }

    public Comparator<String> getComparator() {
        return comparator;
    }

    public Predicate<String> getValidator() {
        return validator;
    }

    public Order getSortingOrder() {
        return sortingOrder;
    }
}
