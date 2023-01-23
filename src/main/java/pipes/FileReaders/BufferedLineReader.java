package pipes.FileReaders;

public interface BufferedLineReader extends AutoCloseable {
    public String nextLine();
}
