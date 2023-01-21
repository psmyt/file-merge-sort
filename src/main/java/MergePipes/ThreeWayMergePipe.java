package MergePipes;

import java.util.Comparator;

import static java.util.Objects.isNull;

public class ThreeWayMergePipe implements Pipe {

    private final Pipe sourceA;
    private final Pipe sourceB;

    private final Comparator<String> comparator;

    ThreeWayMergePipe(Pipe sourceA, Pipe sourceB, Comparator<String> comparator) {
        this.sourceA = sourceA;
        this.sourceB = sourceB;
        this.comparator = comparator;
    }

    @Override
    public String peek() {
        String a = sourceA.peek();
        String b = sourceB.peek();
        if (isNull(a)) return isNull(b) ? null : b;
        if (isNull(b)) return a;
        return comparator.compare(a, b) <= 0 ? a : b;
    }

    @Override
    public String next() {
        String a = sourceA.peek();
        String b = sourceB.peek();
        if (isNull(a)) return isNull(b) ? null : sourceB.next();
        if (isNull(b)) return sourceA.next();
        return comparator.compare(a, b) <= 0 ?
                sourceA.next() : sourceB.next();
    }
}
