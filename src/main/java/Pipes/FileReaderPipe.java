package Pipes;

import Pipes.FileReaders.BufferedLineReader;
import Pipes.FileReaders.BufferedReaderAdapter;
import Pipes.FileReaders.ReverseLineReader;
import Validation.SourceFile;
import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

import static Validation.Order.ASCENDING;

public class FileReaderPipe implements SourcePipe, AutoCloseable {

    private final String filePath;

    private final AutoCloseable source;
    private final BufferedLineReader lineReader;
    private final Queue<String> buffer = new LinkedList<>();

    FileReaderPipe(SourceFile file) {
        this.filePath = file.getName();
        try {
            if (file.getOrder() == ASCENDING) {
                FileReader fileReader = new FileReader(file);
                source = fileReader;
                lineReader = new BufferedReaderAdapter(fileReader);
            } else {
                ReversedLinesFileReader  reverseReader = new ReversedLinesFileReader(file, StandardCharsets.UTF_8); //TODO кодировка?
                source = reverseReader;
                lineReader = new ReverseLineReader(reverseReader);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            String newLine = lineReader.nextLine();
            if (newLine != null) buffer.add(newLine);
            return newLine;
        }
    }

    @Override
    public String next() {
        if (!buffer.isEmpty()) {
            return buffer.remove();
        } else {
            return lineReader.nextLine();
        }
    }

    @Override
    public void close() throws Exception {
        try (AutoCloseable s = source; AutoCloseable r = lineReader) {
            System.out.printf("чтение %s завершено%n", filePath);
        }
    }
}
