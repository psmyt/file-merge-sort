package Pipes;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class FileReaderPipe implements SourcePipe, AutoCloseable {

    private final String filePath;

    private final FileReader fileReader;
    private final BufferedReader bufferedReader;
    private final Queue<String> buffer = new LinkedList<>();

    FileReaderPipe(String filePath) {
        this.filePath = filePath;
        try {
            this.fileReader = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.bufferedReader = new BufferedReader(fileReader);
    }

    @Override
    public String getName() {
        return filePath;
    }

    @Override
    public String peek() {
        if (!buffer.isEmpty()) {
            return buffer.peek();
        } else {
            try {
                String newLine = bufferedReader.readLine();
                if (newLine != null) buffer.add(newLine);
                return newLine;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String next() {
        if (!buffer.isEmpty()) {
            return buffer.remove();
        } else {
            try {
                return bufferedReader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @Override
    public void close() throws Exception {
        try (fileReader; bufferedReader) {}
    }
}
