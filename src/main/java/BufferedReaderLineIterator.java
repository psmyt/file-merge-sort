import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;

public class BufferedReaderLineIterator extends BufferedReader {

    BufferedReaderLineIterator(Reader in, int sz) {
        super(in, sz);
    }

    boolean hasNext() {
        //TODO think of a proper way to deal with readAheadLimit
        try {
            mark(8192);
            if (readLine() == null) return false;
            else {
                reset();
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    String next() {
        try {
            mark(8192);
            return readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void rollBack() {
        try {
            reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void forEachRemaining(Consumer<String> consumer) {
        while (hasNext()) {
            consumer.accept(next());
        }
    }
}
