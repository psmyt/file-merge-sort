package pipes;
import validation.ErrorLogger;
import validation.SourceFile;
import validation.ValidationStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static configuration.Configuration.NUMERIC_COMPARATOR;
import static configuration.Configuration.NUMERIC_VALIDATOR;
import static validation.Order.ASCENDING;
import static org.junit.jupiter.api.Assertions.*;


class FileSourcePipeTest {

    @ParameterizedTest
    @ValueSource(strings = {" ", "-0", "01", "-01", "", "1231-1", "-", "--1", "2247483647\n", "fdsf", "00"})
    void validatorTestFalse(String input) {
        assertFalse(NUMERIC_VALIDATOR.test(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "1111111", "-2222222222222222222222"})
    void validatorTestTrue(String input) {
        assertTrue(NUMERIC_VALIDATOR.test(input));
    }

    @Test
    void fileSourcePipeTest() {
        ErrorLogger logger = new ErrorLogger("src/test/resources/log");
        new Thread(logger).start();
        var factory = new PipeFactory(new ValidationStrategy(NUMERIC_COMPARATOR, NUMERIC_VALIDATOR, ASCENDING), logger);
        try (FileReaderPipe fileReaderPipe = factory.fileReaderPipeInstance(new SourceFile("src/test/resources/file1", ASCENDING));
             SourcePipe validator = factory.validatorPipeInstance(fileReaderPipe)
        ) {
            var expected = Files.readAllLines(Path.of("src/test/resources/file1"))
                    .stream()
                    .filter(NUMERIC_VALIDATOR)
                    .collect(Collectors.toList());
            var actual = Stream.generate(validator::next)
                    .takeWhile(Objects::nonNull)
                    .collect(Collectors.toList());
            assertEquals(expected, actual);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            logger.finishJob();
        }
    }
}