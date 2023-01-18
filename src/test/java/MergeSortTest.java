import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class MergeSortTest {

    Random random = new Random();
    Logger log = Logger.getLogger(MergeSortTest.class.getName());

    Supplier<String> randomIntStringSupplier = () -> String.valueOf(random.nextInt(Integer.MAX_VALUE));

    Predicate<String> naturalNumberValidation = str -> {
        if (str.isEmpty()) return false;
        if (!Character.isDigit(str.charAt(0)) || str.charAt(0) == '0') return false;
        return !str.chars()
                .anyMatch(ch -> !Character.isDigit((char) ch));
    };

    Comparator<String> numericComparator = (a, b) ->
            Integer.valueOf(a).equals(Integer.valueOf(b)) ? 0 :
                    Integer.parseInt(a) > Integer.parseInt(b) ? 1 : -1;

    @Test
    void fileMergeTest() throws IOException {
        generateFile("src/test/resources/file1", 100000);
        generateFile("src/test/resources/file2", 200000);
        log.info("файлы созданы");
        FileLineIterator readerIterator1 =
                new FileLineIterator(new FileReader("src/test/resources/file1"), 4 * 8192,
                        naturalNumberValidation);
        FileLineIterator readerIterator2 =
                new FileLineIterator(new FileReader("src/test/resources/file2"), 4 * 8192,
                        naturalNumberValidation);
        List<String> result = MergeSort.mergeFiles(readerIterator1, readerIterator2, numericComparator);
        log.info(result.toString());
    }

    private void generateFile(String pathString, int lines) throws IOException {
        try (FileWriter fileWriter = new FileWriter(pathString, false)) {
            getOrderedNumericList(lines).forEach(str -> {
                try {
                    if (random.nextInt(100) != 42) fileWriter.write(str + "\n");
                    else fileWriter.write("dfsdfsf dfsfdsf\n\nsdfsdfsf dfsfsd\n");
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

        List<String> expected = Stream.of(list1, list2)
                .flatMap(Collection::stream)
                .sorted(Comparator.comparingInt(Integer::parseInt))
                .collect(Collectors.toList());

        log.info(expected.toString());

        List<String> result = MergeSort.merge(list1.listIterator(), list2.listIterator(),
                (a, b) ->
                        Integer.valueOf(a).equals(Integer.valueOf(b)) ? 0 :
                                Integer.parseInt(a) > Integer.parseInt(b) ? 1 : -1);

        assertEquals(expected, result);
    }

    private List<String> getOrderedNumericList(long size) {
        return Stream.generate(randomIntStringSupplier)
                .limit(size)
                .sorted(Comparator.comparingInt(Integer::parseInt))
                .collect(Collectors.toList());
    }

    @Test
    void mergeFiles() throws IOException {
        generateFile("src/test/resources/file1", 1000);
        generateFile("src/test/resources/file2", 1000);
        generateFile("src/test/resources/file3", 1000);
        FileLineIterator readerIterator1 =
                new FileLineIterator(new FileReader("src/test/resources/file1"), 4 * 8192,
                        naturalNumberValidation);
        FileLineIterator readerIterator2 =
                new FileLineIterator(new FileReader("src/test/resources/file2"), 4 * 8192,
                        naturalNumberValidation);
        FileLineIterator readerIterator3 =
                new FileLineIterator(new FileReader("src/test/resources/file3"), 4 * 8192,
                        naturalNumberValidation);
        PipedWriter pipedWriter = new PipedWriter();
        PipedReader pipedReader = new PipedReader();
        pipedWriter.connect(pipedReader);
        BufferedWriter bufferedWriter = new BufferedWriter(pipedWriter);
        FileLineIterator lineIterator = new FileLineIterator(pipedReader, 8192, naturalNumberValidation);
        BufferedWriter writer = new BufferedWriter(new FileWriter("src/test/resources/result"));
        new Thread(() -> {
            try {
                MergeSort.mergeInputStreams(readerIterator1, readerIterator2, bufferedWriter, numericComparator);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
        MergeSort.mergeInputStreams(lineIterator, readerIterator3, writer, numericComparator);
    }
}