package pipes.FileReaders;

import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ReverseLineReader implements BufferedLineReader {

    static final int BUFFER_SIZE = 1000;

    boolean isOver = false;

    final private ReversedLinesFileReader reader;
    private final Queue<String> buffer = new LinkedList<>();

    public ReverseLineReader(ReversedLinesFileReader reader) {
        this.reader = reader;
    }

    @Override
    public String nextLine() {
        if (buffer.isEmpty()) refillBuffer();
        return buffer.poll();
    }

    private void refillBuffer() {
        try {
            if (isOver) return;
            List<String> nextBatch = reader.readLines(BUFFER_SIZE);
            if (nextBatch.size() < BUFFER_SIZE) isOver = true;
            buffer.addAll(nextBatch);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }
}
