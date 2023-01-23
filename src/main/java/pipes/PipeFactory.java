package pipes;

import validation.ErrorLogger;
import validation.SourceFile;
import validation.ValidationStrategy;

public class PipeFactory {

    private final ValidationStrategy strategy;
    private final ErrorLogger errorLogger;

    public PipeFactory(ValidationStrategy strategy, ErrorLogger errorLogger) {
        this.strategy = strategy;
        this.errorLogger = errorLogger;
    }

    public SortingPipe SortingPipeInstance(Pipe sourceA, Pipe sourceB) {
        return new SortingPipe(sourceA, sourceB, strategy.getComparator());
    }

    public FileReaderPipe fileReaderPipeInstance(SourceFile file) {
        return new FileReaderPipe(file, strategy.getSortingOrder());
    }

    public ValidatorPipe validatorPipeInstance(SourcePipe source) {
        return new ValidatorPipe(strategy, source, errorLogger);
    }


}
