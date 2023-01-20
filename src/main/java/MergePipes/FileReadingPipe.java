package MergePipes;

import Validation.ValidationStrategy;

import java.io.*;
import java.util.Comparator;
import java.util.function.Predicate;

class FileReadingPipe implements Pipe, Closeable {
    public final String filename;
    private final BufferedReader reader;
    private final BufferedWriter writer;

    {
        try {
            writer = new BufferedWriter(new FileWriter("dfsdf"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Comparator<String> comparator;
    private final Predicate<String> validator;

    private String holder;

    public FileReadingPipe(String filePath, ValidationStrategy validationStrategy) {
        this.filename = filePath;
        this.comparator = validationStrategy.getComparator();
        this.validator = validationStrategy.getValidator();
        try {
            this.reader = new BufferedReader(new FileReader(filePath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String peek() {
        return null;
    }

    @Override
    public String next() {
        return null;
    }

    @Override
    public void close() throws IOException {
        try (Closeable r = reader;
             Closeable w = writer) {
        }
    }
}
