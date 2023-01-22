package Pipes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ReceivingPipeTest {

    static Comparator<String> numericComparator = (a, b) ->
            Integer.valueOf(a).equals(Integer.valueOf(b)) ? 0 :
                    Integer.parseInt(a) > Integer.parseInt(b) ? 1 : -1;

    static Random random = new Random();

    Logger log = Logger.getLogger(ReceivingPipeTest.class.getName());

    private static List<String> generateLoad(int size) {
        return Stream.generate(() -> String.valueOf(random.nextInt(10000)))
                .limit(size)
                .sorted(numericComparator)
                .collect(Collectors.toList());
    }

    @Test
    public void threeWayMergePipeTest() {
        var load1 = generateLoad(10000);
        var load2 = generateLoad(10000);
        log.info("start");
        Pipe threeWayPipe = new SortingPipe(new MockSourcePipe(load1),
                new MockSourcePipe(load2),
                numericComparator);
        Stream.of(load1, load2)
                .flatMap(Collection::stream)
                .sorted(numericComparator)
                .forEach(s -> assertEquals(s, threeWayPipe.next()));
    }

    @Test
    public void fourStreams() {
        var load1 = generateLoad(1001);
        var load2 = generateLoad(500);
        var load3 = generateLoad(600);
        var load4 = generateLoad(105);
        var source1 = new MockSourcePipe(load1);
        var source2 = new MockSourcePipe(load2);
        var source3 = new MockSourcePipe(load3);
        var source4 = new MockSourcePipe(load4);
        Pipe threeWay1 = new SortingPipe(source1, source2, numericComparator);
        Pipe threeWay2 = new SortingPipe(source3, source4, numericComparator);
        Pipe result = new SortingPipe(threeWay1, threeWay2, numericComparator);
        Stream.of(load1, load2, load3, load4)
                .flatMap(Collection::stream)
                .sorted(numericComparator)
                .forEach(s -> assertEquals(s, result.next()));
    }

    static Stream<Arguments> sources() {
        return Stream.of(arguments(Stream.of(
                new MockSourcePipe(generateLoad(100000)),
                new MockSourcePipe(generateLoad(100000)),
                new MockSourcePipe(generateLoad(100000)),
                new MockSourcePipe(generateLoad(100000)),
                new MockSourcePipe(generateLoad(100000))).collect(Collectors.toList())));
    }

    @ParameterizedTest
    @MethodSource("sources")
    public void assemblePipes(List<Pipe> sources) throws IOException {
        while (sources.size() > 1) {
            Pipe sourceA = sources.remove(0);
            Pipe sourceB = sources.remove(0);
            sources.add(new SortingPipe(sourceA, sourceB, numericComparator));
        }
        Pipe combinedPipe = sources.get(0);
        try (BufferedWriter writer =
                     new BufferedWriter(
                             new FileWriter("src/test/resources/result"))) {
            IntStream.range(0, 500000).forEach(x -> {
                try {
                    writer.append(combinedPipe.next()).append(System.lineSeparator());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}