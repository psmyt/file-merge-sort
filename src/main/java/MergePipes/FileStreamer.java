package MergePipes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Queue;
import java.util.stream.IntStream;

public class FileStreamer implements Pipe {

    private int size;
    private int offset;
    private Queue<String> buffer;
    Path path;

    private void refillBuffer() {
        try (InputStream inputStream = new FileInputStream(path.toFile());
             BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)
        ) {
            byte[] fill = new byte[size];
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public String peek() {
        if (buffer.isEmpty()) refillBuffer();
        return buffer.peek();
    }

    @Override
    public String next() {
        if (buffer.isEmpty()) refillBuffer();
        return buffer.peek() == null ? null : buffer.remove();
    }
}
