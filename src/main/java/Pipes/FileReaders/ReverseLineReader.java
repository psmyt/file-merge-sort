package Pipes.FileReaders;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class ReverseLineReader implements BufferedLineReader {

    final private ReversedLinesFileReader reader;
    final List<String> buffer = new LinkedList<>();

    public ReverseLineReader(ReversedLinesFileReader reader) {
        this.reader = reader;
    }

    @Override
    public String nextLine() {
        if (buffer.isEmpty()) refillBuffer();
        return buffer.get(0);
    }

    private void refillBuffer() {
        try {
            buffer.addAll(reader.readLines(1000));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        reader.close();
    }
}
