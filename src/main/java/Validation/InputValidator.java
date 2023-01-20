package Validation;

import java.util.Comparator;
import java.util.function.Predicate;

import static Validation.ValidationStatus.*;

public class InputValidator {

    final Predicate<String> validator;
    final Comparator<String> comparator;

    String previousValidLine;

    public InputValidator(ValidationStrategy validationStrategy) {
        validator = validationStrategy.getValidator();
        comparator = validationStrategy.getComparator();
    }

    public ValidationStatus validate(String line) {
        if (previousValidLine != null) {
            return validator.test(line) && comparator.compare(line, previousValidLine) >= 0 ? VALID :
                    validator.test(line) ? OUT_OF_ORDER : INVALID;
        } else {
            if (validator.test(line)) {
                previousValidLine = line;
                return VALID;
            } else return INVALID;
        }
    }
}
