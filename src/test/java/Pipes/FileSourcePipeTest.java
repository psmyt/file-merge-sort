package Pipes;

import Validation.ValidationStrategy;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;
import java.util.stream.IntStream;

class FileSourcePipeTest {
    Predicate<String> naturalNumberValidation = str -> {
        if (str.isEmpty()) return false;
        if (!Character.isDigit(str.charAt(0)) || str.charAt(0) == '0') return false;
        return !str.chars()
                .anyMatch(ch -> !Character.isDigit((char) ch));
    };
    static Comparator<String> numericComparator = (a, b) ->
            Integer.valueOf(a).equals(Integer.valueOf(b)) ? 0 :
                    Integer.parseInt(a) > Integer.parseInt(b) ? 1 : -1;

    @Test
    void fileSourcePipeTest() {
        BlockingQueue<String> log = new ArrayBlockingQueue<>(1000);
        var factory = new PipeFactory(new ValidationStrategy(numericComparator, naturalNumberValidation), log);
        try (FileReaderPipe fileReaderPipe = factory.fileReaderPipeInstance("src/test/resources/file1")) {
            Pipe validator = factory.validatorPipeInstance(fileReaderPipe);
            IntStream.range(0, 100).forEach(x -> System.out.println(validator.next()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.forEach(System.out::println);
    }
}