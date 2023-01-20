package Validation;

import java.util.Comparator;
import java.util.function.Predicate;

public class ValidationStrategy {
    private final Comparator<String> comparator;

    private final Predicate<String> validator;

    public ValidationStrategy(Comparator<String> comparator, Predicate<String> validator) {
        this.comparator = comparator;
        this.validator = validator;
    }

    public Comparator<String> getComparator() {
        return comparator;
    }

    public Predicate<String> getValidator() {
        return validator;
    }
}
