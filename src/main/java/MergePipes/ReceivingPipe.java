package MergePipes;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ReceivingPipe implements Pipe {
    private final Pipe source;
    private final BlockingQueue<String> buffer;

    public ReceivingPipe(Pipe source) {
        this.source = source;
        this.buffer = new ArrayBlockingQueue<>(100);
    }

    @Override
    public String peek() {
        // TODO синхронизация??
        synchronized (this) {
            try {
                String next = buffer.isEmpty() ? source.next() : buffer.take();
                buffer.add(next);
                return next;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String next() {
        synchronized (this) {
            try {
                return buffer.isEmpty() ? source.next() : buffer.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
