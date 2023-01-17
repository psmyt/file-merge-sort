import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class MergeSortTest {

    Random random = new Random();
    Logger log = Logger.getLogger("MergeSortTest");

    Supplier<String> randomIntStringSupplier = () -> String.valueOf(random.nextInt(Integer.MAX_VALUE));

    @Test
    void fileMergeTest() throws IOException {
        Comparator<String> numericComparator = (a, b) ->
                Integer.valueOf(a).equals(Integer.valueOf(b)) ? 0 :
                        Integer.parseInt(a) > Integer.parseInt(b) ? 1 : -1;

        generateFile("src/test/resources/file1", 100);
        generateFile("src/test/resources/file2", 200);
        BufferedReaderLineIterator readerIterator1 =
                new BufferedReaderLineIterator(new FileReader("src/test/resources/file1"), 8192);
        BufferedReaderLineIterator readerIterator2 =
                new BufferedReaderLineIterator(new FileReader("src/test/resources/file2"), 8192);
        List<String> result = MergeSort.mergeFiles(readerIterator1, readerIterator2, numericComparator);
        log.info(result.toString());
    }

    private void generateFile(String pathString, int lines) throws IOException {
        try (FileWriter fileWriter = new FileWriter(pathString, false)) {
            getOrderedNumericList(lines).forEach(str -> {
                try {
                    fileWriter.write(str + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    static Stream<Arguments> listSizes() {
        return Stream.of(
                arguments(100, 200),
                arguments(200, 100),
                arguments(220, 221),
                arguments(2200, 221),
                arguments(220, 2210),
                arguments(2, 2210),
                arguments(58888, 2210)
        );
    }

    @ParameterizedTest
    @MethodSource("listSizes")
    void numericMergeTest(int list1Size, int list2Size) {
        List<String> list1 = getOrderedNumericList(list1Size);

        log.info(list1.toString());

        List<String> list2 = getOrderedNumericList(list2Size);

        log.info(list2.toString());

        Comparator<String> numericComparator = (a, b) ->
                Integer.valueOf(a).equals(Integer.valueOf(b)) ? 0 :
                        Integer.parseInt(a) > Integer.parseInt(b) ? 1 : -1;

        List<String> expected = Stream.of(list1, list2)
                .flatMap(Collection::stream)
                .sorted(Comparator.comparingInt(Integer::parseInt))
                .collect(Collectors.toList());

        log.info(expected.toString());

        List<String> result = MergeSort.merge(list1.listIterator(), list2.listIterator(),
                numericComparator);

        assertEquals(expected, result);
    }

    private List<String> getOrderedNumericList(long size) {
        return Stream.generate(randomIntStringSupplier)
                .limit(size)
                .sorted(Comparator.comparingInt(Integer::parseInt))
                .collect(Collectors.toList());
    }
}