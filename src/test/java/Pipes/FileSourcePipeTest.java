package Pipes;

import Validation.ErrorLogger;
import Validation.SourceFile;
import Validation.ValidationStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Character.isDigit;
import static org.junit.jupiter.api.Assertions.*;


class FileSourcePipeTest {
    public static Comparator<String> NUMERIC_COMPARATOR = (a, b) ->
            Integer.valueOf(a).equals(Integer.valueOf(b)) ? 0 :
                    Integer.parseInt(a) > Integer.parseInt(b) ? 1 : -1;

    public static Predicate<String> NUMERIC_VALIDATOR = FileSourcePipeTest::numericValidator;

    public static boolean numericValidator(String str) {
        if (str.isEmpty()) return false;
        if (str.equals("0")) return true;
        char firstSymbol = str.charAt(0);
        if (firstSymbol == '-') return str.charAt(1) != '0' && numericValidator(str.substring(1));
        if (firstSymbol == '0' && str.length() > 1) return false;
        return IntStream.range(0, str.length()).allMatch(i -> isDigit(str.charAt(i)));
    }

    Predicate<String> naturalNumberValidation = str -> {
        if (str.isEmpty()) return false;
        if (!Character.isDigit(str.charAt(0)) || str.charAt(0) == '0') return false;
        return str.chars().allMatch(ch -> Character.isDigit((char) ch));
    };
    static Comparator<String> numericComparator = (a, b) ->
            Integer.valueOf(a).equals(Integer.valueOf(b)) ? 0 :
                    Integer.parseInt(a) > Integer.parseInt(b) ? 1 : -1;

    @ParameterizedTest
    @ValueSource(strings = {" ", "-0", "01", "", "1231-1", "--1", "2247483647\n", "fdsf", "00"})
    void validatorTestFalse(String input) {
        assertFalse(NUMERIC_VALIDATOR.test(input));
    }

    @ParameterizedTest
    @ValueSource(strings = {"10", "-1", "", "1231-1", "--1", "2247483647\n", "fdsf", " "})
    void validatorTestTrue(String input) {
        assertFalse(NUMERIC_VALIDATOR.test(input));
    }

    @Test
    void fileSourcePipeTest() {
        var factory = new PipeFactory(new ValidationStrategy(numericComparator, naturalNumberValidation),
                new ErrorLogger("src/test/resources/file1"));
        try (FileReaderPipe fileReaderPipe = factory.fileReaderPipeInstance(new SourceFile("src/test/resources/file1"));
             SourcePipe validator = factory.validatorPipeInstance(fileReaderPipe)
        ) {
            var expected = Files.readAllLines(Path.of("src/test/resources/file1"))
                    .stream()
                    .filter(naturalNumberValidation)
                    .sorted(numericComparator)
                    .collect(Collectors.toList());
            var actual = Stream.generate(validator::next)
                    .takeWhile(Objects::nonNull)
                    .collect(Collectors.toList());
            assertEquals(expected, actual);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}