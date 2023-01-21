package Validation;

import java.util.function.Predicate;

public class StringValidator implements Predicate<String> {
    @Override
    public boolean test(String s) {
        return false;
    }
}
