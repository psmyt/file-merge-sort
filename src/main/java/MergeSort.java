import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

public class MergeSort {

    public static List<String> merge(ListIterator<String> listIterator1,
                                     ListIterator<String> listIterator2,
                                     Comparator<String> comparator) {
        List<String> result = new ArrayList<>();
        while (listIterator1.hasNext()) {
            String a = listIterator1.next();
            while (listIterator2.hasNext()) {
                String b = listIterator2.next();
                if (comparator.compare(a, b) >= 0) {
                    result.add(b);
                } else {
                    listIterator2.previous();
                    break;
                }
            }
            result.add(a);
        }
        listIterator2.forEachRemaining(result::add);
        return result;
    }
}
