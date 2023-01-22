package Pipes;

public interface SourcePipe extends Pipe, AutoCloseable {
    String getName();
}
