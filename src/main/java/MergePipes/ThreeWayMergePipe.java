package MergePipes;

import java.util.Comparator;

class ThreeWayMergePipe implements Pipe {

    private final Pipe sourceA;
    private final Pipe sourceB;

    private final Comparator<String> comparator;

    public ThreeWayMergePipe(Pipe sourceA, Pipe sourceB, Comparator<String> comparator) {
        this.sourceA = sourceA;
        this.sourceB = sourceB;
        this.comparator = comparator;
    }

    @Override
    public String peek() {
        String a = sourceA.peek();
        String b = sourceB.peek();
        if (isPoison(a)) return isPoison(b) ? POISON : b;
        if (isPoison(b)) return isPoison(a) ? POISON : a;
        return comparator.compare(a, b) <= 0 ? a : b;
    }

    @Override
    public String next() {
        String a = sourceA.peek();
        String b = sourceB.peek();
        if (isPoison(a)) return isPoison(b) ? POISON : sourceB.next();
        if (isPoison(b)) return isPoison(a) ? POISON : sourceA.next();
        return comparator.compare(a, b) <= 0 ?
                sourceA.next() : sourceB.next();
    }
}
