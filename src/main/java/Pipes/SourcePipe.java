package Pipes;

import Pipes.Pipe;

public interface SourcePipe extends Pipe, AutoCloseable {
    String getName();
}
