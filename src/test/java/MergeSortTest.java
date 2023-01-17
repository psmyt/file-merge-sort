import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MergeSortTest {

    @Test
    void mergeTest() {
        List<String> list1 = List.of("5", "44", "54", "66", "1555");
        List<String> list2 = List.of("1", "15", "15", "43", "100", "210", "230", "577");

        Comparator<String> numericComparator = (a,b) ->
                Integer.valueOf(a).equals(Integer.valueOf(b))? 0 :
                        Integer.parseInt(a) > Integer.parseInt(b)? 1 : -1;

        List<String> expected = Stream.of(list1,list2)
                .flatMap(Collection::stream)
                .sorted(numericComparator)
                .collect(Collectors.toList());

        System.out.println(expected);

        List<String> result = MergeSort.merge(list1.listIterator(), list2.listIterator(),
                numericComparator);

        assertEquals(expected, result);
    }
}