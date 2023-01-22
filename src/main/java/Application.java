import Pipes.ListOfSourcePipes;
import Pipes.PipeFactory;
import Pipes.Pipe;
import Validation.ErrorLogger;
import Validation.SourceFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Application {

    final PipeFactory pipeFactory;

    final List<SourceFile> files;

    final ErrorLogger logger = new ErrorLogger("src/test/resources/log.txt");

    Application(String[] args) {
        pipeFactory = new PipeFactory(Configuration.resolveValidationStrategy(args), logger);
        files = Configuration.processFileNames(args);
    }

    public static void main(String[] args) throws IOException {
        new Application(args).execute();
    }

    private void execute() throws IOException {
        new Thread(logger).start();
        try (FileWriter fileWriter = new FileWriter(files.get(0));
             BufferedWriter writer = new BufferedWriter(fileWriter);
             ListOfSourcePipes inputs = preparePipes(files);
        ) {
            Pipe output = assemblePipes(inputs);
            String nextLine;
            while ((nextLine = output.next()) != null) {
                writer.append(nextLine).append(System.lineSeparator());
            }
            logger.finish();
        }
    }

    ListOfSourcePipes preparePipes(List<SourceFile> files) {
        return files.stream()
                .skip(1)
                .map(pipeFactory::fileReaderPipeInstance)
                .map(pipeFactory::validatorPipeInstance)
                .collect(Collectors.toCollection(ListOfSourcePipes::new));
    }

    Pipe assemblePipes(ListOfSourcePipes sources) {
        List<Pipe> pipes = new ArrayList<>(sources);
        while (pipes.size() > 1) {
            Pipe sourceA = pipes.remove(0);
            Pipe sourceB = pipes.remove(0);
            pipes.add(pipeFactory.SortingPipeInstance(sourceA, sourceB));
        }
        return pipes.get(0);
    }

}
