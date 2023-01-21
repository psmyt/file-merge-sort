package MergePipes;

import Validation.ValidationStrategy;

import java.util.concurrent.BlockingQueue;

public class PipeFactory {

    private final ValidationStrategy strategy;
    private final BlockingQueue<String> errorLog;

    public PipeFactory(ValidationStrategy strategy, BlockingQueue<String> errorLog) {
        this.strategy = strategy;
        this.errorLog = errorLog;
    }

    public ThreeWayMergePipe threeWayMergePipeInstance(Pipe sourceA, Pipe sourceB) {
        return new ThreeWayMergePipe(sourceA, sourceB, strategy.getComparator());
    }

    public FileReaderPipe fileReaderPipeInstance(String filePath) {
        return new FileReaderPipe(filePath);
    }

    public InputValidatorPipe inputValidatorPipeInstance(NamedPipe source) {
        return new InputValidatorPipe(strategy, source, errorLog);
    }


}
