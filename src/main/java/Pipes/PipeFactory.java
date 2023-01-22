package Pipes;

import Validation.ValidationStrategy;

import java.util.concurrent.BlockingQueue;

public class PipeFactory {

    private final ValidationStrategy strategy;
    private final BlockingQueue<String> errorLog;

    public PipeFactory(ValidationStrategy strategy, BlockingQueue<String> errorLog) {
        this.strategy = strategy;
        this.errorLog = errorLog;
    }

    public SortingPipe SortingPipeInstance(Pipe sourceA, Pipe sourceB) {
        return new SortingPipe(sourceA, sourceB, strategy.getComparator());
    }

    public FileReaderPipe fileReaderPipeInstance(String filePath) {
        return new FileReaderPipe(filePath);
    }

    public ValidatorPipe validatorPipeInstance(SourcePipe source) {
        return new ValidatorPipe(strategy, source, errorLog);
    }


}
