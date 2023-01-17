import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

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
        List<String> list1 = Stream.generate(randomIntStringSupplier)
                .limit(list1Size)
                .sorted(Comparator.comparingInt(Integer::parseInt))
                .collect(Collectors.toList());

        log.info(list1.toString());

        List<String> list2 = Stream.generate(randomIntStringSupplier)
                .limit(list2Size)
                .sorted(Comparator.comparingInt(Integer::parseInt))
                .collect(Collectors.toList());

        log.info(list2.toString());

        Comparator<String> numericComparator = (a, b) ->
                Integer.valueOf(a).equals(Integer.valueOf(b)) ? 0 :
                        Integer.parseInt(a) > Integer.parseInt(b) ? 1 : -1;

        List<String> expected = Stream.of(list1, list2)
                .flatMap(Collection::stream)
                .sorted(numericComparator)
                .collect(Collectors.toList());

        log.info(expected.toString());

        List<String> result = MergeSort.merge(list1.listIterator(), list2.listIterator(),
                numericComparator);

        assertEquals(expected, result);
    }
}