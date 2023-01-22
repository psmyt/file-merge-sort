package Pipes.FileReaders;

public interface BufferedLineReader extends AutoCloseable {
    public String nextLine();
}
