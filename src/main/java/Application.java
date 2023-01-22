import Pipes.ListOfSourcePipes;
import Pipes.PipeFactory;
import Pipes.Pipe;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class Application {

    final PipeFactory pipeFactory;

    final List<String> files;
    final BlockingQueue<String> errorLog = new ArrayBlockingQueue<>(1000);

    Application(String[] args) {
        Configuration configuration = new Configuration(args);
        pipeFactory = new PipeFactory(configuration.getValidationStrategy(), errorLog);
        files = configuration.getFileNames();
    }

    public static void main(String[] args) throws IOException {
        new Application(args).execute();
    }

    private void execute() throws IOException {
        try (FileWriter fileWriter = new FileWriter(files.get(0));
             BufferedWriter writer = new BufferedWriter(fileWriter);
             ListOfSourcePipes inputs = preparePipes(files.subList(1, files.size()));
        ) {
            Pipe output = assemblePipes(inputs);
            String nextLine;
            while ((nextLine = output.next()) != null) {
                writer.append(nextLine).append(System.lineSeparator());
            }
            System.out.println(errorLog);
        }
    }

    public BlockingQueue<String> getErrorLog() {
        return errorLog;
    }

    ListOfSourcePipes preparePipes(List<String> files) {
        return files.stream()
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
