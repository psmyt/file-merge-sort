import MergePipes.PipeFactory;
import MergePipes.Pipe;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Application {

    final PipeFactory pipeFactory;

    final Stream<String> files;
    final BlockingQueue<String> errorLog = new ArrayBlockingQueue<>(10);
    Application(String[] args) {
        Configuration configuration = new Configuration(args);
        pipeFactory = new PipeFactory(configuration.getValidationStrategy(), errorLog);
        files = configuration.getFileNames();
    }

    public static void main(String[] args) throws IOException {
        new Application(args).start();
    }

    private void start() throws IOException {
        List<Pipe> inputs = preparePipes(files.skip(1));
        Pipe output = assemblePipes(inputs);
        try (FileWriter fileWriter = new FileWriter(files.limit(1).findAny().get());
             BufferedWriter writer = new BufferedWriter(fileWriter)
        ) {
            String nextLine = "";
            while (nextLine != null) {
                nextLine = output.next();
                writer.append(nextLine).append(System.lineSeparator());
            }
        }
    }

    public BlockingQueue<String> getErrorLog() {
        return errorLog;
    }

    List<Pipe> preparePipes(Stream<String> files) {
        return files
                .map(pipeFactory::fileReaderPipeInstance)
                .map(pipeFactory::inputValidatorPipeInstance)
                .collect(Collectors.toList());
    }

    Pipe assemblePipes(List<Pipe> sources) {
        while (sources.size() > 1) {
            Pipe sourceA = sources.remove(0);
            Pipe sourceB = sources.remove(0);
            sources.add(pipeFactory.threeWayMergePipeInstance(sourceA, sourceB));
        }
        return sources.get(0);
    }

}
