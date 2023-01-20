package MergePipes;

import Validation.ValidationStrategy;

public class Factory {

    private final ValidationStrategy strategy;

    Factory(ValidationStrategy strategy) {
        this.strategy = strategy;
    }

    public ThreeWayMergePipe threeWayMergePipeInstance(Pipe sourceA, Pipe sourceB) {
        return new ThreeWayMergePipe(sourceA, sourceB, strategy.getComparator());
    }

    public FileReadingPipe fileReadingPipeInstance(String filePath) {
        return new FileReadingPipe(filePath, strategy);
    }


}
