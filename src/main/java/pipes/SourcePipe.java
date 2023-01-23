package pipes;

public interface SourcePipe extends Pipe, AutoCloseable {
    String getName();
}
