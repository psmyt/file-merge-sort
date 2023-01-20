package Validation;

import java.util.Comparator;

public class NumericComparator implements Comparator<String> {

    @Override
    public int compare(String s1, String s2) {

        return 0;
    }

    @Override
    public Comparator<String> reversed() {
        return Comparator.super.reversed();
    }
}
