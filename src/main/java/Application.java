import configuration.Configuration;
import pipes.ListOfSourcePipes;
import pipes.PipeFactory;
import pipes.Pipe;
import validation.ErrorLogger;
import validation.SourceFile;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
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

    void execute() throws IOException {
        System.out.printf("начало %s %n", Instant.now().atZone(ZoneId.systemDefault()));
        new Thread(logger).start();
        try (FileWriter fileWriter = new FileWriter(files.get(0), true);
             BufferedWriter writer = new BufferedWriter(fileWriter);
             ListOfSourcePipes inputs = prepareSourcePipes(files);
        ) {
            Pipe output = assemblePipes(inputs);
            String nextLine = output.next();
            writer.append(nextLine);
            while ((nextLine = output.next()) != null) {
                writer.append(System.lineSeparator())
                        .append(nextLine);
            }
        } finally {
            logger.finishJob();
        }
    }

    ListOfSourcePipes prepareSourcePipes(List<SourceFile> files) {
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
