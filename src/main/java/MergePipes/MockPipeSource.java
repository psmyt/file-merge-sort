package MergePipes;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class MockPipeSource implements Pipe {
    private final Queue<String> supply = new LinkedList<>();
    private final Random random = new Random();

    MockPipeSource(Collection<String> load) {
        supply.addAll(load);
    }

    @Override
    public String peek() {
        return supply.peek() != null ? supply.peek() : POISON;
    }

    @Override
    public String next() {
        //            Thread.sleep(random.nextInt(100));
        return supply.size() != 0 ? supply.remove() : POISON;
    }
}
